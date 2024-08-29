package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Long duration; //пока предполагаем продолжительность в минутах

    public Film() {

    }

    public Film(String name, String description, LocalDate localDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = localDate;
        this.duration = duration;
    }

    public Film(long id, String name, String description, LocalDate localDate, Long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = localDate;
        this.duration = duration;
    }
}
