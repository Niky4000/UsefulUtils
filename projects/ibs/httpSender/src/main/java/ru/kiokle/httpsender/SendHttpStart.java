package ru.kiokle.httpsender;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import ru.ibs.utils.utils.MapBuilder;

public class SendHttpStart {

	public static void main(String[] args) throws Exception {
		String userSessionId = new String(sendPost("http://localhost:8089/ufs-client/createStubSession/startSession", "GET", null, MapBuilder.<String, String>builder().put("ufs-client-ip", "0.0.0.0").build()));
		System.out.println(userSessionId);
//		String response = new String(sendPost("http://localhost:8089/ufs-client/workflow-gate?cmd=START&name=open-mock-deposit", "POST", "{\n"
//				+ "    \"ownerId\" : \"97852156885215482\",\n"
//				+ "    \"currency\" : \"RUB\"\n"
//				+ "}",
//				MapBuilder.<String, String>builder().put("Cookie", "UFS-SESSION=" + userSessionId + "; Path=/; Secure; HttpOnly'")
//						.put("Content-Type", "application/json").build()));
		String response = new String(sendPost("http://localhost:8089/ufs-client/workflow-gate?cmd=START&name=close-mock-deposit", "POST", "{\n"
				+ "    \"ownerId\" : \"97852156885215482\",\n"
				+ "    \"currency\" : \"RUB\"\n"
				+ "}",
				MapBuilder.<String, String>builder().put("Cookie", "UFS-SESSION=" + userSessionId + "; Path=/; Secure; HttpOnly'")
						.put("Content-Type", "application/json").build()));
		System.out.println(response);
	}

	private static final String USER_AGENT = "Apache-HttpClient/4.1.1 (java 1.5)";

	private static byte[] sendPost(String url, String requestMethod, String params, Map<String, String> headers) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "UTF-8");
		for (Entry<String, String> entry : headers.entrySet()) {
			con.setRequestProperty(entry.getKey(), entry.getValue());
		}

		con.setDoOutput(true);
		if (params != null) {
			try ( OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream())) {
				outputStreamWriter.write(params.toString());
				outputStreamWriter.flush();
			}
		}
		int responseCode = con.getResponseCode();
//		System.out.println("\nSending 'POST' request to URL : " + url);
//        System.out.println("Post parameters : " + urlParameters);
//		System.out.println("Response Code : " + responseCode);
		if (responseCode == 200) {
			return readInputStream(new BufferedInputStream(con.getInputStream()));
		} else {
			return readInputStream(new BufferedInputStream(con.getErrorStream()));
		}
	}

	private static final int BUFFER_SIZE = 1024 * 1024;
	private static final int SMALL_BUFFER_SIZE = 1024 * 1024;

	public static byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		byte[] bytes3 = null;
		int previousIndex = 0;
		try ( BufferedInputStream in = new BufferedInputStream(inputStream)) {
			Integer read = 0;
			Integer available = 0;
			do {
				available = in.available();
				byte[] bytes2 = new byte[available > SMALL_BUFFER_SIZE ? available : SMALL_BUFFER_SIZE];
				read = in.read(bytes2);
				if (read > 0 && read + previousIndex > bytes.length) {
					int newBufferSize = Math.max(bytes2.length, BUFFER_SIZE + bytes.length + previousIndex);
					bytes3 = new byte[newBufferSize];
					System.arraycopy(bytes, 0, bytes3, 0, bytes.length);
					bytes = new byte[newBufferSize];
					System.arraycopy(bytes3, 0, bytes, 0, bytes3.length);
					System.arraycopy(bytes2, 0, bytes, previousIndex, read);
					previousIndex += read;
				} else if (read > 0) {
					System.arraycopy(bytes2, 0, bytes, previousIndex, read);
					previousIndex += read;
				}
			} while (read > 0);
		}
		bytes3 = new byte[previousIndex];
		System.arraycopy(bytes, 0, bytes3, 0, previousIndex);
		return bytes3;
	}

}
