package com.projectfloyd.Q1043;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Q1043Application {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Q1043Application.class);
		application.setAdditionalProfiles("ssl");
		application.run(args);
		//SpringApplication.run(Q1043Application.class, args);
	}

}
