package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class FilmorateApplication {

    public static void main(String[] args) {
        log.info("Server started");
        SpringApplication.run(FilmorateApplication.class, args);
    }

}
