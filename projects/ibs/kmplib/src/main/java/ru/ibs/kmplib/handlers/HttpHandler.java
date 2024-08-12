package ru.ibs.kmplib.handlers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс, осуществляющий непосредственное взаимодействие по https с внещним
 * сервисом! Посылает json-запросы!
 *
 * @author me
 */
public class HttpHandler {

	private static final Logger log = LoggerFactory.getLogger("kmp");
	private static final int HTTP_READ_TIMEOUT = 60 * 1000;
	private static final String USER_AGENT = "Apache-HttpClient/4.1.1 (java 1.5)";

	@SuppressWarnings("all")
	public <K> K sendPost(String urlStr, Object obj, Class<K> objClass) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(HTTP_READ_TIMEOUT);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setDoOutput(true);
			try (OutputStream outputStream = con.getOutputStream()) {
				marshallToTheOutputStream(outputStream, obj); // Объект сразу сериализуем в выходной поток! А иначе были отмечены какие-то странные глюки!
				outputStream.flush();
			}
			int responseCode = con.getResponseCode();
			byte[] bytes;
			if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
				try (InputStream in = con.getInputStream()) {
					bytes = IOUtils.toByteArray(in);
				}
			} else {
				try (InputStream in = con.getErrorStream()) {
					bytes = IOUtils.toByteArray(in);
				}
			}
			if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
				String httpResponse = handleHttpResponseString(bytes);
				byte[] encodeToUtf8 = httpResponse.getBytes(StandardCharsets.UTF_8);
				try {
					K kk = unmarshall(encodeToUtf8, objClass);
					return kk;
				} catch (Exception e) {
					log.error("sendPost Exception!", e);
					throw new RuntimeException(e);
				}
			} else {
				String string = new String(bytes, "utf-8");
				throw new RuntimeException(string);
			}
		} catch (Exception e) {
			log.error("sendPost Exception!", e);
			throw new RuntimeException(e);
		}
	}

	private void marshallToTheOutputStream(OutputStream outputStream, Object obj) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.writeValue(outputStream, obj);
	}

	// It's for debug purpose!
	@SuppressWarnings("all")
	@Deprecated
	public <K> K sendPost(String urlStr, String urlParameters, Class<K> objClass) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(HTTP_READ_TIMEOUT);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			con.setRequestProperty("User-Agent", USER_AGENT);
//			String urlParameters = handleMarshalledObject(marshall(obj));
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters.replace("Т", "\\Т"));
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			byte[] bytes = null;
			if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
				try (InputStream in = con.getInputStream()) {
					bytes = IOUtils.toByteArray(in);
				}
			} else {
				try (InputStream in = con.getErrorStream()) {
					bytes = IOUtils.toByteArray(in);
				}
			}
			if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
				String httpResponse = handleHttpResponseString(bytes);
				byte[] encodeToUtf8 = httpResponse.getBytes(StandardCharsets.UTF_8);
				try {
					K kk = unmarshall(encodeToUtf8, objClass);
					return kk;
				} catch (Exception e) {
					e.printStackTrace();
					File file = new File("C:\\tmp\\kmp\\response.txt");
					if (file.exists()) {
						file.delete();
					}
					Files.write(file.toPath(), encodeToUtf8, StandardOpenOption.CREATE_NEW);
					throw new RuntimeException(e);
				}
			} else {
				File file = new File("C:\\tmp\\kmp\\request.txt");
				if (file.exists()) {
					file.delete();
				}
				Files.write(file.toPath(), urlParameters.getBytes(), StandardOpenOption.CREATE_NEW);
				String string = new String(bytes, "utf-8");
				throw new RuntimeException(string);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private String handleMarshalledObject(String obj) {
//		return obj.replace("Т", "\\Т");
		return obj;
	}

	private String handleHttpResponseString(byte[] bytes) throws UnsupportedEncodingException {
		return new String(bytes);
	}

	public byte[] marshall2(Object obj) throws JAXBException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return objectMapper.writeValueAsBytes(obj);
	}

	public String marshall(Object obj) throws JAXBException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		return objectMapper.writeValueAsString(obj);
	}

	public <T> T unmarshall(byte[] bytes, Class<T> objClass) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(bytes, objClass);
	}
}
