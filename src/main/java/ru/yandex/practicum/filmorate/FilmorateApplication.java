package ru.yandex.practicum.filmorate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
	private static final Logger log = LoggerFactory.getLogger(FilmorateApplication.class);

	public static void main(String[] args) {
		log.info("Server started");
		SpringApplication.run(FilmorateApplication.class, args);
	}

}
