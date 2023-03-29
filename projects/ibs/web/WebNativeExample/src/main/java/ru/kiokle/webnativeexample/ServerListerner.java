package ru.kiokle.webnativeexample;

import com.utils.WaitUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerListerner {

	private final int port;
	private AtomicBoolean shutdown = new AtomicBoolean(false);
	private static final int TIME_TO_WAIT = 60;
	private final ExecutorService executorService = Executors.newCachedThreadPool();
	private final int BUFFER_SIZE = 1024 * 1024;
	private static final String HEADER_END = "\r\n\r\n";
	private static final String SPACE = " ";
	private static final String POST = "POST";
	private static final String GET = "GET";

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
//						socket.setSoTimeout(400);
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
				try ( BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream(), BUFFER_SIZE); //
						  BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream(), BUFFER_SIZE);) {
					int read = 0;
					byte[] buffer = new byte[1024];
					while (read >= 0 && inputStream.available() > 0) {
						read = inputStream.read(buffer);
						byteArrayOutputStream.write(buffer);
					}
					String request = new String(byteArrayOutputStream.toByteArray());
					String data = request.substring(request.indexOf(HEADER_END) + HEADER_END.length());
					String httpMethod = getHttpMethod(request);
					String url = getUrl(request, httpMethod);
					String response = createResponse(data, httpMethod, url);
					StringBuilder sb = new StringBuilder("HTTP/1.1 200 OK\n").append("Date: ").append(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date())).append("\n").append("Content-length: ").append(response.length()).append(HEADER_END).append(response);
					String string = sb.toString();
					outputStream.write(string.getBytes());
					outputStream.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static String createResponse(String data, String httpMethod, String url) {
		if (GET.equals(httpMethod)) {
			return new StringBuilder("Http method: ").append(httpMethod).append(" Received Data: ").append(url).toString();
		} else if (POST.equals(httpMethod)) {
			return new StringBuilder("Http method: ").append(httpMethod).append(" Received Data: ").append(data).toString();
		} else {
			return "Unknown http method!";
		}
	}

	private String getHttpMethod(String request) {
		return request.substring(0, request.indexOf(SPACE));
	}

	private String getUrl(String request, String httpMethod) {
		return URLDecoder.decode(request.substring(httpMethod.length() + SPACE.length(), request.indexOf(SPACE, httpMethod.length() + SPACE.length())), Charset.forName("utf-8"));
	}
}
