package ru.ibs.socketserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author me
 */
public class SocketServerStart {

	private static final int BUFFER_SIZE = 1024 * 1024;

	public static void main(String[] args) throws IOException {
		SocketServerStart socketServerHandler = new SocketServerStart();
		List<String> argList = Arrays.asList(args);
		if (!argList.isEmpty()) {
			String dir = socketServerHandler.getParameter(argList, "-dir");
			Integer port = Integer.valueOf(socketServerHandler.getParameter(argList, "-port"));
			new File(dir).mkdirs();
			ExecutorService executorService = Executors.newCachedThreadPool();
			try (ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getByName("localhost"))) {
				while (true) {
					Socket socket = serverSocket.accept();
					executorService.submit(() -> socketServerHandler.socketHandler(socket, new File(dir)));
				}
			}
		}
	}

	private String getParameter(List<String> argList, String argName) {
		return argList.get(argList.indexOf(argName) + 1);
	}

	private void socketHandler(final Socket socket, File dir) {
		try {
			System.out.println("port=" + socket.getPort());
			File file = new File(dir.getAbsolutePath() + File.separator + "file_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS").format(new Date()));
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(BUFFER_SIZE);
			try {
				InputStream inputStream = socket.getInputStream();
				byte[] b = new byte[BUFFER_SIZE];
				int read = 0;
				do {
					read = inputStream.read(b);
					if (read > 0) {
						byteArrayOutputStream.write(b, 0, read);
					}
				} while (read > 0 && read >= BUFFER_SIZE);
			} finally {
				socket.close();
			}
			if (file.exists()) {
				file.delete();
			}
			Files.write(file.toPath(), byteArrayOutputStream.toByteArray(), StandardOpenOption.CREATE_NEW);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static void bytesTest() throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
		byteArrayOutputStream.write(createBytes((byte) 80));
		byteArrayOutputStream.write(createBytes((byte) 40));
		byteArrayOutputStream.write(createBytes((byte) -40));
		byteArrayOutputStream.write(createBytes((byte) 20));
		System.out.println(byteArrayOutputStream.toByteArray().length);
	}

	private static byte[] createBytes(byte filler) {
		byte[] bytes = new byte[1024];
		Arrays.fill(bytes, filler);
		return bytes;
	}
}
