package us.codecentro.sometestproject3;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/url-controller")
public class TestController {

//	@Value
	private static final String URL = "http://localhost:8080/"; // + guid

	private Map<String, String> urlMap = new ConcurrentHashMap<>();
	private Map<String, String> uuidMap = new ConcurrentHashMap<>();

	private String shortenUrl(String url) {
		String uuid = urlMap.computeIfAbsent(url, str -> {
			String uuid_ = URL + UUID.randomUUID().toString();
			uuidMap.put(uuid_, url);
			return uuid_;
		});
		return uuid;
	}

	@PostMapping("/shorten-url")
	@ResponseBody
	public String shortenUrlMethod(@RequestBody String url) {
		System.out.println(url);
		String uuid = shortenUrl(url);
		return uuid;
	}

	@PostMapping("/get-url")
	@ResponseBody
	public String getUrlMethod(@RequestBody String uuid) {
		String url = uuidMap.get(uuid);
		// sending email
		return url;
	}
}
