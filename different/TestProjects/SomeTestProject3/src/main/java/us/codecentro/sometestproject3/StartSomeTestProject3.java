package us.codecentro.sometestproject3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StartSomeTestProject3 {

	public static void main(String[] args) {
		SpringApplication.run(StartSomeTestProject3.class, args);
		System.out.println("Hello World!!!");
		// http://codenco.us/very-long-string-fkfdonsdjkbfkjsdbkdsnbfkdsnbfksdbfdskbdekjblnfd
		// -> http://codenco.us/short-string-kjjj
	}

	private Map<String, String> urlMap = new HashMap<>();

	private String shortenUrl(String url) {
		String uuid = UUID.randomUUID().toString(); // 36 symbols
		urlMap.put(uuid, url);
		return null;
	}
}
