package ru.kiokle.webnativeexample;

import com.utils.WaitUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerListerner {

	private final int port;
	private AtomicBoolean shutdown = new AtomicBoolean(false);
	private static final int TIME_TO_WAIT = 60;
	ExecutorService executorService = Executors.newCachedThreadPool();

	public ServerListerner(int port) {
		this.port = port;
	}

	public void listen() throws IOException {
		Thread serverListernerThread = new Thread(() -> {
			while (true) {
				try {
					ServerSocket serverSocket = new ServerSocket(port);
					while (!shutdown.get()) {
						Socket socket = serverSocket.accept();
						socket.setSoTimeout(400);
						socketHandler(socket);
					}
				} catch (Exception e) {
					WaitUtils.waitSomeTime(TIME_TO_WAIT);
					continue;
				}
			}
		});
		serverListernerThread.setName("serverListernerThread");
		serverListernerThread.start();
	}

	private void socketHandler(Socket socket) {
		executorService.submit(() -> {
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				try ( BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());  BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());) {
					int read = 0;
					byte[] buffer = new byte[1024];
					while (read >= 0) {
						read = inputStream.read(buffer);
						byteArrayOutputStream.write(buffer);
					}
					StringBuilder sb = new StringBuilder("Data: ").append(new String(byteArrayOutputStream.toByteArray()));
					outputStream.write(sb.toString().getBytes());
					outputStream.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
