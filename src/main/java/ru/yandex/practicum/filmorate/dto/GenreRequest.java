package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GenreRequest {

    @NotBlank(message = "Признак жанра не может быть пустым.")
    String name;

    String description;
}
