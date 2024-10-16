package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.validators.ReleaseDateConstraint;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmRequest {

    @Positive(message = "Id фильма должен быть положительным числом")
    Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    @NotNull(message = "Название фильма не может быть пустым")
    String name;

    @Size(message = "Слишком длинное описание фильма (более 200 символов)", max = 200)
    String description;

    @ReleaseDateConstraint(message = "Некорректная дата релиза фильма")
    LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма должна быть положительным числом")
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    Long duration; //пока предполагаем продолжительность в минутах

    @NotNull(message = "Рейтинг МПА должен быть указан")
    Rating mpa;

    List<Genre> genres; //Для получения списка жанров

    public FilmRequest(String name, String description, LocalDate releaseDate, Long duration, Rating mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }
}
