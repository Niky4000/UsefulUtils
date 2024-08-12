package ru.kiokle.webembeddedexample;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebEmbeddedExampleStart {

	public static void main(String[] args) throws Exception {
		System.out.println("Hello World!!!");
		HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(10);
		server.createContext("/api/test-controller/", new HttpHandler() {
			@Override
			public void handle(HttpExchange httpExchange) throws IOException {
				String requestParamValue = null;
				if ("GET".equals(httpExchange.getRequestMethod())) {
					requestParamValue = handleGetRequest(httpExchange);
				} else if ("POST".equals(httpExchange.getRequestMethod())) {
					requestParamValue = handlePostRequest(httpExchange);
				}
				handleResponse(httpExchange, requestParamValue);
			}

			private String handleGetRequest(HttpExchange httpExchange) {
				return httpExchange.getRequestURI().toString().split("\\?")[1].split("=")[1].replace("%20", " ");
			}

			private String handlePostRequest(HttpExchange httpExchange) throws IOException {
				return new String(httpExchange.getRequestBody().readAllBytes());
			}

			private void handleResponse(HttpExchange httpExchange, String requestParamValue) throws IOException {
				try ( OutputStream outputStream = httpExchange.getResponseBody()) {
					StringBuilder htmlBuilder = new StringBuilder();
					htmlBuilder.append("<html>").append("<body>").append("<h1>").append("Hello ").append(requestParamValue)
							.append("</h1>").append("</body>").append("</html>");
					// encode HTML content 
//            String htmlResponse = StringEscapeUtils.escapeHtml4(htmlBuilder.toString());
					String htmlResponse = htmlBuilder.toString();
// this line is a must
					httpExchange.sendResponseHeaders(200, htmlResponse.length());
					outputStream.write(htmlResponse.getBytes());
					outputStream.flush();
				}
			}
		});
		server.setExecutor(newFixedThreadPool);
		server.start();
//logger.info(" Server started on port 8001");
	}
}
