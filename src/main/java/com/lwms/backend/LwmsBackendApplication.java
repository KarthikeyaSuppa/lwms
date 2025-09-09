package com.lwms.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LwmsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LwmsBackendApplication.class, args);
		System.out.println("Started");
	}

}	
