package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    //Аннотации перенесены в класс UserRequest
    Long id; // уникальный идентификатор пользователя,

    String email; //электронная почта пользователя

    String login; //логин пользователя

    String name; //имя пользователя для отображения,

    LocalDate birthday; //дата рождения

    //Множество id друзей в порядке увеличения id
    Set<Long> friends = new TreeSet<>((l1, l2) -> Math.toIntExact(l1 - l2));

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

}
