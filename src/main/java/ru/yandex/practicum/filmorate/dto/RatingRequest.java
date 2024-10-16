package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RatingRequest {
    Long id;

    @NotBlank(message = "Признак MPA-рейтинга не может быть пустым.")
    String name;

    String description;
}
