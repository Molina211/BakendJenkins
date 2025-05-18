package com.corhuila.backReservasUH;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackReservasUhApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackReservasUhApplication.class, args);
	}

}
