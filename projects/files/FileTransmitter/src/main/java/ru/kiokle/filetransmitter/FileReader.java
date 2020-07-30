/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.filetransmitter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import static ru.kiokle.filetransmitter.FileTransmitter.BUFFER_SIZE;
import ru.kiokle.filetransmitter.bean.FileDataBean;
import ru.kiokle.filetransmitter.bean.FileStatusBean;

/**
 *
 * @author Me
 */
public class FileReader extends Thread {

    private H2Handler h2 = new H2Handler();
    private static final MyLogger LOG = new MyLogger();
    private final String targetDir;
    private final File targetDirAsFile;
    private final String s;
    private final int port;
    private final CommonLogic commonLogic = new CommonLogic();

    public FileReader(String h2DbLocation, String targetDir, int port) {
        super("FileReader");
        this.targetDir = targetDir;
        this.port = port;
        targetDirAsFile = new File(targetDir);
        targetDirAsFile.mkdirs();
        s = FileSystems.getDefault().getSeparator();
        h2.createDatabase(h2DbLocation);
    }

    @Override
    public void run() {
        try {
            AtomicBoolean stop = new AtomicBoolean(false);
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (stop.get()) {
                    LOG.log("Reader finished!", MyLogger.LogLevel.DEBUG);
                    break;
                }
                LOG.log("clientSocket accepted!!! " + clientSocket.getRemoteSocketAddress().toString() + "!", MyLogger.LogLevel.DEBUG);
                Thread thread = new Thread(() -> {
                    try {
                        try (InputStream inputStream = new BufferedInputStream(clientSocket.getInputStream(), BUFFER_SIZE);
                                OutputStream outputStream = clientSocket.getOutputStream();) {
                            if (readFromSocket(inputStream, outputStream)) {
                                stop.set(true);
                                Socket socket = new Socket("localhost", port);
                                socket.close();
                            }
                        } finally {
                            clientSocket.close();
                        }
                    } catch (IOException io) {
                        LOG.log("Local thread exception!", io);
                    }
                });
                thread.start();
            }
        } catch (IOException ex) {
            LOG.log("IOException!", ex);
        }
    }

    private boolean readFromSocket(InputStream inputStream, OutputStream outputStream) {
        FileDataBean fileDataBean = null;
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                byteOutputStream.write(buffer, 0, bytesRead);
                byteOutputStream.flush();
            }
            byte[] data = byteOutputStream.toByteArray();
            LOG.log("data with length = " + data.length + " was read from socket!", MyLogger.LogLevel.DEBUG);
            if (data.length > 0) {
                Object deSerializeObject = CommonLogic.deSerializeObject(data, LOG);
                if (deSerializeObject instanceof FileDataBean) {
                    fileDataBean = (FileDataBean) deSerializeObject;
                    LOG.log("fileDataBean  fileData recieved: " + CommonLogic.printData(fileDataBean.getData()) + " PackageNumber: " + fileDataBean.getPackageNumber()
                            + " FileRelativePath: " + (fileDataBean.getFileRelativePath() != null ? fileDataBean.getFileRelativePath() : "")
                            + " data length: " + (fileDataBean.getData() != null ? fileDataBean.getData().length : 0)
                            + " FileLength: " + fileDataBean.getFileLength() + " PackagesCount: " + fileDataBean.getPackagesCount() + "!", MyLogger.LogLevel.DEBUG);
                    writeToTheEndOfTheFile(fileDataBean);
                    byte[] serializeObject = CommonLogic.serializeObject(new FileStatusBean(0L, "", "", "", true), LOG);
                    outputStream.write(serializeObject);
                    outputStream.flush();
                    return fileDataBean != null && fileDataBean.getFileRelativePath() == null;
                } else if (deSerializeObject instanceof FileStatusBean) {
                    if (md5Thread == null || !md5Thread.isAlive()) {
                        Callable<FileStatusBean> callable = () -> {
                            FileStatusBean fileStatusBean = (FileStatusBean) deSerializeObject;
                            File transferedFile = new File(targetDir + s + fileStatusBean.getFileRelativePath());
                            String md5Sum = commonLogic.getMd5Sum(transferedFile);
                            String md5 = fileStatusBean.getMd5();
                            for (int i = 0; i < 100; i++) {
                                if (md5Sum.equals(md5)) {
                                    break;
                                } else {
                                    smallWait();
                                    md5Sum = commonLogic.getMd5Sum(transferedFile);
                                }
                                LOG.log("md5Sum.equals(md5) waiting!", MyLogger.LogLevel.DEBUG);
                            }
                            return new FileStatusBean(transferedFile.length(), md5Sum, fileStatusBean.getFileRelativePath(), transferedFile.getAbsolutePath(), true);
                        };
                        md5Task = new FutureTask(callable);
                        md5Thread = new Thread(md5Task);
                        md5Thread.setName("md5Thread");
                        md5Thread.start();
                        return tryToReturnTheResult(outputStream);
                    } else {
                        return tryToReturnTheResult(outputStream);
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return fileDataBean != null && fileDataBean.getFileRelativePath() == null;
    }

    private boolean tryToReturnTheResult(OutputStream outputStream) throws IOException {
        try {
            FileStatusBean fileStatusBean = md5Task.get(1, TimeUnit.SECONDS);
            byte[] serializeObject = CommonLogic.serializeObject(fileStatusBean, LOG);
            outputStream.write(serializeObject);
            outputStream.flush();
        } catch (TimeoutException | InterruptedException ex) {
            LOG.log("Result is not ready!", MyLogger.LogLevel.DEBUG);
            FileStatusBean fileStatusBeanLocal = new FileStatusBean(null, null, null, null, false);
            byte[] serializeObject = CommonLogic.serializeObject(fileStatusBeanLocal, LOG);
            outputStream.write(serializeObject);
            outputStream.flush();
        } catch (ExecutionException ex) {
            LOG.log("tryToReturnTheResult Exception!", ex);
        }
        return false;
    }

    FutureTask<FileStatusBean> md5Task;
    Thread md5Thread;

    private void writeToTheEndOfTheFile(FileDataBean fileDataBean) {
        if (fileDataBean != null && fileDataBean.getFileRelativePath() != null && fileDataBean.getData() != null) {
            File file = new File(targetDir + s + fileDataBean.getFileRelativePath());
            try {
                if (fileDataBean.getPackageNumber() == 0) {
                    if (file.exists()) {
                        file.delete();
                    }
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    file.createNewFile();
                    while (file.length() > 0) {
                        Files.write(file.toPath(), new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
                        smallWait();
                    }
                }
                if (fileDataBean.getData().length > 0) {
//                    Files.write(file.toPath(), fileDataBean.getData(), StandardOpenOption.APPEND);
//                    try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
//                        outputStream.write(fileDataBean.getData());
//                        outputStream.flush();
//                    }
                    Long oldFileLength = Long.valueOf(file.length());
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(oldFileLength);
                    randomAccessFile.write(fileDataBean.getData());
                    randomAccessFile.close();
                    Long newFileLength = Long.valueOf(file.length());
                    while (!newFileLength.equals(oldFileLength + Integer.valueOf(fileDataBean.getData().length).longValue())) {
                        smallWait();
                        newFileLength = Long.valueOf(file.length());
                        LOG.log("Waiting...", MyLogger.LogLevel.DEBUG);
                    }
                }
                if (Integer.valueOf(fileDataBean.getPackageNumber()).equals(fileDataBean.getPackagesCount() - 1)) {
                    long fileLength = file.length();
                    String md5Sum = commonLogic.getMd5Sum(file);
                    h2.save(file.getAbsolutePath(), md5Sum, fileLength, true);
                }
            } catch (IOException e) {
//                throw new RuntimeException("writeToTheEndOfTheFile Exception!", e);
                LOG.log("writeToTheEndOfTheFile exception!", e);
            }
        }
    }

    private void smallWait() {
        try {
            Thread.sleep(SMALL_TIME_TO_WAIT);
        } catch (InterruptedException ex) {
            LOG.log("Interrupted!", MyLogger.LogLevel.DEBUG);
        }
    }
    private static final int SMALL_TIME_TO_WAIT = 100;
}
