package com.CPS.web;

import com.CPS.web.services.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebApplication {

	@Autowired
	private ServiceImpl weatherService;

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}
}
