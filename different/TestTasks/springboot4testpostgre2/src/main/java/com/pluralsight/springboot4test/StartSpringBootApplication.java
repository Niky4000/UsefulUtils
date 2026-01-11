package com.pluralsight.springboot4test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author me
 */
@SpringBootApplication
public class StartSpringBootApplication {

    public static void main(String[] args) {
        System.out.println("Hello!");
        SpringApplication.run(StartSpringBootApplication.class, args);
    }
}
