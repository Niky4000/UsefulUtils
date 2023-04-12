package com.element.springbootandbirt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

@SpringBootApplication
@EnableScheduling
@Service
public class StartSpringBootAndBirt {

	@Autowired
	BirtService birtService;

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		System.out.println("Hello world, I have just started up!");
		birtService.run();
	}

	public static void main(String[] args) {
		SpringApplication.run(StartSpringBootAndBirt.class, args);
	}
}
