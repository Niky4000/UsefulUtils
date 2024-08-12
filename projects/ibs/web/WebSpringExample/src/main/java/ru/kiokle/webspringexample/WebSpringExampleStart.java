package ru.kiokle.webspringexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebSpringExampleStart {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(WebSpringExampleStart.class, args);
		System.out.println("Hello World!!!");
	}
}
