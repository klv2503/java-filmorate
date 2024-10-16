package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Rating {
    Long id;
    String name;
    String description;

    public Rating(Long mpaId) {
        this.id = mpaId;
    }

}
