package ru.kiokle.webexampleclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class WebExampleClientStart {

	public static void main(String[] args) throws Exception {
		String startResult = new WebExampleClientStart().sendPost("http://localhost:8080/api/test-controller/test?str=Hello%20" + new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss:SSS").format(new Date()) + "!", RequestMethod.GET, getAuthMap(), null);
		System.out.println(startResult);
		String startResult2 = new WebExampleClientStart().sendPost("http://localhost:8080/api/test-controller/test2", RequestMethod.POST, getAuthMap(), "Hello! Hello post method " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()) + "!");
		System.out.println(startResult2);
	}

	private static LinkedHashMap<String, String> getAuthMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		map.put("Content-Type", "application/json");
		map.put("User-Agent", USER_AGENT);
		return map;
	}
	private static final String USER_AGENT = "Apache-HttpClient/4.1.1 (java 1.5)";
	private static final int READ_TIMEOUT = 60000;

	private enum RequestMethod {
		GET, POST
	}

	@SuppressWarnings("all")
	private String sendGet(String urlStr, RequestMethod requestMethod, Map<String, String> headers) throws Exception {
		return sendPost(urlStr, requestMethod, headers, null);
	}

	// HTTP POST request
	@SuppressWarnings("all")
	private String sendPost(String urlStr, RequestMethod requestMethod, Map<String, String> headers, String urlParameters) throws Exception {
		URL url = new URL(urlStr);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setReadTimeout(READ_TIMEOUT);
		con.setRequestMethod(requestMethod.name());
		headers.entrySet().forEach(entry -> con.setRequestProperty(entry.getKey(), entry.getValue()));
		con.setDoOutput(true);
		if (requestMethod.equals(RequestMethod.POST)) {
			try ( OutputStream wr = con.getOutputStream()) {
				wr.write(urlParameters.getBytes());
				wr.flush();
			}
		}
		int responseCode = con.getResponseCode();
		InputStream errorStream = getErrorStream(con);
		try ( InputStream inputStream = (errorStream != null ? errorStream : getInputStream(con))) {
			StringBuffer response = readResponse(con, inputStream);
			if (responseCode != java.net.HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Response code = " + responseCode + "!");
			}
			return response.toString();
		}
	}

	private InputStream getInputStream(HttpURLConnection con) {
		try {
			return con.getInputStream();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private InputStream getErrorStream(HttpURLConnection con) {
		return con.getErrorStream();
	}

	private StringBuffer readResponse(HttpURLConnection con, InputStream inputStream) throws IOException {
		StringBuffer response = new StringBuffer();
		try ( BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		return response;
	}
}
