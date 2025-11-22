package com.br.SAM_FullStack.SAM_FullStack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@SpringBootApplication
@EnableScheduling
public class SamFullStackApplication {

	public static void main(String[] args) {
		SpringApplication.run(SamFullStackApplication.class, args);
	}

}

	