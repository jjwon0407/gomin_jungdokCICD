package com.gomin_jungdok.gdgoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class GdgocApplication {

	public static void main(String[] args) {
		SpringApplication.run(GdgocApplication.class, args);
	}

}
