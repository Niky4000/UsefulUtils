/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.filetransmitter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import ru.kiokle.filetransmitter.bean.FileDataBean;
import ru.kiokle.filetransmitter.bean.FileSizeBean;
import ru.kiokle.filetransmitter.bean.FileStatusBean;

/**
 *
 * @author Me
 */
public class FileTransmitter extends Thread {

	private H2Handler h2 = new H2Handler();
	private static final MyLogger LOG = new MyLogger();
	private LinkedBlockingQueue<FileDataBean> dataQueue = new LinkedBlockingQueue<>(1);
	private final CommonLogic commonLogic = new CommonLogic();
	public static final int BUFFER_SIZE = 1024 * 1024;
	private static final int TIME_TO_WAIT_IN_CASE_OF_ERROR = 10 * 1000;
	private final String baseDir;
	private final String fileToTransmit;
	private final String server;
	private final int port;

	public FileTransmitter(String h2DbLocation, String baseDir, String fileToTransmit, String server, int port, boolean deleteExisting) {
		super("FileTransmitter");
		if (!fileToTransmit.startsWith(baseDir)) {
			throw new RuntimeException("fileToTransmit should start with the baseDir!");
		}
		this.fileToTransmit = fileToTransmit;
		this.server = server;
		this.port = port;
		this.baseDir = baseDir;
		h2.createDatabase(h2DbLocation, deleteExisting);
	}

	@Override
	public void run() {
		sendFile(server, port);
		readDir(new File(fileToTransmit));
		finish();
		LOG.log("FileTransmitter finished!");
	}

	private void sendFile(String server, int port) {
		Thread thread = new Thread(() -> {
			FileDataBean fileDataBean = null;
			do {
				try {
					fileDataBean = dataQueue.take();
					Socket socket = new Socket(server, port);
					socket.setSoTimeout(0);
					socket.setKeepAlive(true);
					try (InputStream inputStream = new BufferedInputStream(socket.getInputStream(), BUFFER_SIZE);
						OutputStream outputStream = socket.getOutputStream();) {
						byte[] serializeObject = CommonLogic.serializeObject(fileDataBean, LOG);
						outputStream.write(serializeObject);
						outputStream.flush();
						socket.shutdownOutput();
						if (fileDataBean.getData() != null || fileDataBean.getFileRelativePath() != null) {
							byte[] buffer = new byte[BUFFER_SIZE];
							inputStream.read(buffer);
							FileStatusBean fileStatusBean = (FileStatusBean) CommonLogic.deSerializeObject(buffer, LOG);
							LOG.log("fileStatusBean = " + fileStatusBean.toString() + "!", MyLogger.LogLevel.DEBUG);
						}
						LOG.log("data with length = " + serializeObject.length + " was written to socket!", MyLogger.LogLevel.DEBUG);
					}
					socket.close();
				} catch (IOException ex) {
					LOG.log("Cannot open socket!", ex);
				} catch (InterruptedException ex) {
					LOG.log("Interrupted!", ex);
				}
			} while (fileDataBean.getData() != null);
		}
		);
		thread.setName("sendFile");
		thread.start();
	}

	private void readDir(File baseDir) {
		if (baseDir.exists() && baseDir.isDirectory()) {
			Arrays.stream(baseDir.listFiles()).forEach(file -> {
				final AtomicReference<FileSizeBean> result = new AtomicReference<>();
				Supplier<FileSizeBean> existsSupplier = () -> result.updateAndGet((obj) -> obj != null ? obj : h2.exists(file.getAbsolutePath())); // Local cache for the poor!
				boolean check = true;
				String md5Sum = null;
				Long fileSize = 0L;
				try {
					if (file.isDirectory() && !existsSupplier.get().isCheck()) {
						readDir(file);
//						if (file.listFiles().length == 0) {
//							file.delete();
//							LOG.log("dirrectory " + file.getAbsolutePath() + " was deleted because it was empty!");
//						}
					} else {
						FileSizeBean fileSizeBean = readFileAndCheckIt(file, existsSupplier);
						if (!fileSizeBean.isCheck()) {
							check = false;
						}
						fileSize += fileSizeBean.getFileSize();
						md5Sum = fileSizeBean.getMd5Sum();
					}
				} catch (Exception ex) {
					LOG.log(file.getAbsolutePath() + " Exception occured!", ex);
				}
				h2.save(file.getAbsolutePath(), md5Sum, fileSize, check);
			}
			);
		} else if (baseDir.exists() && baseDir.isFile()) {
			FileSizeBean fileSizeBean = readFileAndCheckIt(baseDir, () -> h2.exists(baseDir.getAbsolutePath()));
			Long fileSize = fileSizeBean.getFileSize();
			String md5Sum = fileSizeBean.getMd5Sum();
			boolean check = fileSizeBean.isCheck();
			h2.save(baseDir.getAbsolutePath(), md5Sum, fileSize, check);
		} else {
			LOG.log("File " + baseDir.getAbsolutePath() + " does not exist!");
		}
	}

	private void finish() {
		try {
			dataQueue.put(new FileDataBean(new byte[]{}, null, 0, 0, 0));
			dataQueue.put(new FileDataBean(null, null, 0, 0, 0));
		} catch (InterruptedException ex) {
			LOG.log("dataQueue InterruptedException!", ex);
		}
	}

	private FileSizeBean readFileAndCheckIt(File file, Supplier<FileSizeBean> existsSupplier) {
		FileSizeBean exists = existsSupplier.get();
		if (!exists.isCheck()) {
			boolean check = false;
			String md5Sum = null;
			do {
				md5Sum = commonLogic.getMd5Sum(file);
				long length = file.length();
				try {
					readFile(file, md5Sum);
				} catch (Exception e) {
					LOG.log("readFile Exception!", e);
					waitSomeTime();
				}
				FileStatusBean fileStatusBean = check(file, length, md5Sum);
				check = fileStatusBean != null ? fileStatusBean.getMd5().equals(md5Sum) : false;
//				h2.save(file.getAbsolutePath(), md5Sum, file.length(), check);
				if (!check) {
					LOG.log("Check failed for " + file.getAbsolutePath() + "!");
					if (fileStatusBean != null) {
						LOG.log("fileStatusBean md5Sum=" + fileStatusBean.getMd5() + " but original md5Sum=" + md5Sum + "!");
						LOG.log("fileStatusBean length=" + fileStatusBean.getLength().toString() + " but original md5Sum=" + length + "!");
						LOG.log("Absolute path " + fileStatusBean.getAbsolutePath() + "!");
						LOG.log("Original file " + file.getAbsolutePath() + "!");
						String md5Sum1 = commonLogic.getMd5Sum(file);
						String md5Sum2 = commonLogic.getMd5Sum(new File(fileStatusBean.getAbsolutePath()));
						boolean equals = md5Sum1.equals(md5Sum2);
					}
				}
			} while (!check);
			return new FileSizeBean(check, file.length(), md5Sum);
		} else {
			return exists;
		}
	}

	private FileStatusBean check(File file, long length, String md5Sum) {
		FileStatusBean fileStatusBean = new FileStatusBean(null, null, null, null, false);
		while (!fileStatusBean.isReady()) {
			boolean status = false;
			Socket socket = null;
			try {
				socket = new Socket(server, port);
				socket.setSoTimeout(0);
				socket.setKeepAlive(true);
				try (OutputStream stream = socket.getOutputStream();
					InputStream inputStream = socket.getInputStream();) {
					byte[] serializeObject = CommonLogic.serializeObject(new FileStatusBean(length, md5Sum, getRelativePath(file), file.getAbsolutePath(), true), LOG);
					LOG.log("data with length = " + serializeObject.length + " was written to socket for check!", MyLogger.LogLevel.DEBUG);
					stream.write(serializeObject, 0, serializeObject.length);
					stream.flush();
					socket.shutdownOutput();
					byte[] buffer = new byte[BUFFER_SIZE];
					inputStream.read(buffer);
					fileStatusBean = (FileStatusBean) CommonLogic.deSerializeObject(buffer, LOG);
					status = fileStatusBean.getMd5().equals(md5Sum);
				}
				if (!status) {
					waitSomeTime();
				}
			} catch (IOException ex) {
				LOG.log("Cannot open socket!", ex);
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException ex) {
						LOG.log("socket close exception!", ex);
					}
				}
			}
		}
		return fileStatusBean;
	}

	private void waitSomeTime() {
		try {
			Thread.sleep(TIME_TO_WAIT_IN_CASE_OF_ERROR);
		} catch (InterruptedException ex) {
			LOG.log("Wait exception!", ex);
		}
	}

	private void readFile(File file, String md5Sum) {
		if (file.length() > 0L) {
			byte[] buffer = new byte[BUFFER_SIZE];
			try (FileInputStream fis = new FileInputStream(file)) {
				int fileLength = fis.available();
				int packageNumber = 0;
				int packagesCount = BigDecimal.valueOf(fileLength).divide(BigDecimal.valueOf((long) BUFFER_SIZE), RoundingMode.CEILING).intValue();
				Integer bytesRead;
				while ((bytesRead = fis.read(buffer)) != -1) {
					byte[] buffer2 = new byte[bytesRead];
					System.arraycopy(buffer, 0, buffer2, 0, bytesRead);
					FileDataBean fileDataBean = new FileDataBean(buffer2, getRelativePath(file), fileLength, packageNumber++, packagesCount);
					logFileDataBean(fileDataBean);
					while (true) {
						try {
							dataQueue.put(fileDataBean);
							break;
						} catch (InterruptedException ex) {
							LOG.log("dataQueue InterruptedException!", ex);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			FileDataBean fileDataBean = new FileDataBean(new byte[0], getRelativePath(file), 0, 0, 1);
			logFileDataBean(fileDataBean);
			while (true) {
				try {
					dataQueue.put(fileDataBean);
					break;
				} catch (InterruptedException ex) {
					LOG.log("dataQueue InterruptedException!", ex);
				}
			}
		}
	}

	private void logFileDataBean(FileDataBean fileDataBean) {
		LOG.log("fileDataBean" + "fileData transmitted: " + CommonLogic.printData(fileDataBean.getData()) + " PackageNumber: " + fileDataBean.getPackageNumber()
			+ " FileRelativePath: " + fileDataBean.getFileRelativePath() + " data length: " + fileDataBean.getData().length
			+ " FileLength: " + fileDataBean.getFileLength() + " PackagesCount: " + fileDataBean.getPackagesCount() + "!", MyLogger.LogLevel.DEBUG);
	}

	private String getRelativePath(File file) {
		String path = file.getAbsolutePath().replace(baseDir, "").replace("\\", "/");
		return path.startsWith("/") ? path.substring(1) : path;
	}
}
