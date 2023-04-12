package us.codecentro.sometestproject3;

import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/url-controller")
public class UrlController {

	@GetMapping("/test")
	@ResponseBody
	public String test(@RequestParam("str") String str) {
		System.out.println(str);
		return UUID.randomUUID().toString();
	}
}
