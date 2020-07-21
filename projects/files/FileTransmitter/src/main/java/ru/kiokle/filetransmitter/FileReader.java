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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicBoolean;
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
                    LOG.log("Reader finished!");
                    break;
                }
                LOG.log("clientSocket accepted!!! " + clientSocket.getRemoteSocketAddress().toString() + "!");
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
            LOG.log("data with length = " + data.length + " was read from socket!");
            if (data.length > 0) {
                Object deSerializeObject = CommonLogic.deSerializeObject(data, LOG);
                if (deSerializeObject instanceof FileDataBean) {
                    fileDataBean = (FileDataBean) deSerializeObject;
                    LOG.log("fileDataBean" + "fileData recieved: " + CommonLogic.printData(fileDataBean.getData()) + " PackageNumber: " + fileDataBean.getPackageNumber()
                            + " FileRelativePath: " + (fileDataBean.getFileRelativePath() != null ? fileDataBean.getFileRelativePath() : "")
                            + " data length: " + (fileDataBean.getData() != null ? fileDataBean.getData().length : 0)
                            + " FileLength: " + fileDataBean.getFileLength() + " PackagesCount: " + fileDataBean.getPackagesCount() + "!");
                    writeToTheEndOfTheFile(fileDataBean);
                    byte[] serializeObject = CommonLogic.serializeObject(new FileStatusBean("", ""), LOG);
                    outputStream.write(serializeObject);
                    outputStream.flush();
                    return fileDataBean == null || fileDataBean.getFileRelativePath() == null;
                } else if (deSerializeObject instanceof FileStatusBean) {
                    FileStatusBean fileStatusBean = (FileStatusBean) deSerializeObject;
                    String md5Sum = CommonLogic.getMd5Sum(new File(targetDir + s + fileStatusBean.getFileRelativePath()));
                    FileStatusBean fileStatusBeanLocal = new FileStatusBean(md5Sum, fileStatusBean.getFileRelativePath());
                    byte[] serializeObject = CommonLogic.serializeObject(fileStatusBeanLocal, LOG);
                    outputStream.write(serializeObject);
                    outputStream.flush();
                    return false;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return fileDataBean == null || fileDataBean.getFileRelativePath() == null;
    }

    private void writeToTheEndOfTheFile(FileDataBean fileDataBean) {
        if (fileDataBean != null && fileDataBean.getFileRelativePath() != null && fileDataBean.getData() != null) {
            File file = new File(targetDir + s + fileDataBean.getFileRelativePath());
            try {
                if (fileDataBean.getPackageNumber() == 0) {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                if (fileDataBean.getData().length > 0) {
                    Files.write(file.toPath(), fileDataBean.getData(), StandardOpenOption.APPEND);
                }
                if (Integer.valueOf(fileDataBean.getPackageNumber()).equals(fileDataBean.getPackagesCount() - 1)) {
                    String md5Sum = CommonLogic.getMd5Sum(file);
                    h2.save(file.getAbsolutePath(), md5Sum, file.length(), true);
                }
            } catch (IOException e) {
//                throw new RuntimeException("writeToTheEndOfTheFile Exception!", e);
                LOG.log("writeToTheEndOfTheFile exception!", e);
            }
        }
    }
}
