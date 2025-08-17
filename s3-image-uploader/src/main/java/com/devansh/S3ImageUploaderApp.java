package com.devansh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class S3ImageUploaderApp {

	public static void main(String[] args) {
		SpringApplication.run(S3ImageUploaderApp.class, args);
	}

}
