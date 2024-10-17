package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.validators.UsersLoginConstraint;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserRequest {

    @Positive(message = "Users id must be positive")
    Long id;

    //Поскольку контроллер теперь принимает userRequest, аннотации перенес в класс UserRequest
    @Email(message = "Некорректный e-mail пользователя")
    @NotBlank(message = "Некорректный e-mail пользователя")
    private String email;

    @UsersLoginConstraint
    private String login;

    private String name;

    @NotNull(message = "Некорректная дата рождения")
    @PastOrPresent(message = "Некорректная дата рождения")
    private LocalDate birthday;

    public UserRequest(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public boolean hasName() {
        return !((name == null) || (name.isBlank()));
    }
}
