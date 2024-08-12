package ru.kiokle.webspringexample.controller;

import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-controller")
public class TestController {

	@GetMapping("/test")
	@ResponseBody
	public String test(@RequestParam("str") String str) {
		System.out.println(str);
		return UUID.randomUUID().toString();
	}

	@PostMapping("/test2")
	@ResponseBody
	public String test2(@RequestBody String str) {
		System.out.println(str);
		return UUID.randomUUID().toString();
	}
}
