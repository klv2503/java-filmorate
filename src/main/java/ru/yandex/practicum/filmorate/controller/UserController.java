package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicateData;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public @ResponseBody User create(@Valid @RequestBody User user) {
        if (isUsedLogin(user.getLogin())) {
            log.warn("\nNot created {}", user);
            throw new DuplicateData("Этот login уже используется", user);
        }
        if (isUsedEmail(user.getEmail())) {
            log.warn("\nNot created {}", user);
            throw new DuplicateData("Этот e-mail уже используется", user);
        }
        // формируем дополнительные данные
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        log.info("\nSuccessfully created {}", user);
        return user;
    }

    @PutMapping
    public @ResponseBody User update(@RequestBody User newUser) {
        // Метод пока оставил без правок. Хотелось бы принять методологию
        // "Если пользователь найден, то корректные данные из запроса применяются, некорректные игнорируются".
        // Но тогда @Valid использовать не получается, т.к. получится исключение и отказ в изменениях. Уверен,
        // что валидацию аннотациями и здесь можно реализовать, но пока не знаю как
        if (newUser.getId() == null) {
            log.warn("\nNot updated {}", newUser);
            throw new NotFoundException("Id должен быть указан", newUser);
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getLogin() == null || newUser.getLogin().isBlank()
                    || newUser.getLogin().contains(" ")) {
                log.warn("\nNot updated {}", newUser);
                return oldUser;
            }
            if (!oldUser.getLogin().equals(newUser.getLogin()) && isUsedLogin(newUser.getLogin())) {
                log.warn("\nNot updated {}", newUser);
                throw new DuplicateData("Этот login уже используется", newUser);
            }
            if (newUser.getEmail() == null || newUser.getEmail().isBlank()
                    || !newUser.getEmail().contains("@") ||
                    newUser.getEmail().indexOf("@") != newUser.getEmail().lastIndexOf("@")) {
                log.warn("\nNot updated {}", newUser);
                return oldUser;
            }
            if (!oldUser.getEmail().equals(newUser.getEmail()) && isUsedEmail(newUser.getEmail())) {
                log.warn("\nNot updated {}", newUser);
                throw new DuplicateData("Этот e-mail уже используется", newUser);
            }
            // проверяем валидность даты рождения
            if (newUser.getBirthday() != null) {
                try {
                    LocalDate usersDate = newUser.getBirthday();
                } catch (RuntimeException e) {
                    log.warn("\nNot updated {}", newUser);
                    throw new ValidationException("Некорректные данные даты рождения", newUser);
                }
            }
            // если пользователь найден и данные валидны, обновляем данные
            oldUser.setLogin(newUser.getLogin());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                oldUser.setName(newUser.getLogin());
            } else
                oldUser.setName(newUser.getName());
            oldUser.setEmail(newUser.getEmail());
            if (newUser.getBirthday() != null) {
                if (!LocalDate.now().isBefore(newUser.getBirthday()))
                    oldUser.setBirthday(newUser.getBirthday());
            }
            log.info("\nSuccessfully updated {}.", oldUser);
            return oldUser;
        }
        log.warn("\nNot updated {}", newUser);
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден", newUser);
    }

    private boolean isUsedLogin(String login) {
        //Метод проверяет, не занят ли логин другим пользователем
        return users.keySet()
                .stream()
                .map(users::get)
                .anyMatch(user -> user.getLogin().equals(login));
    }

    private boolean isUsedEmail(String email) {
        //Метод проверяет, не занят ли e-mail другим пользователем
        return users.keySet()
                .stream()
                .map(users::get)
                .anyMatch(user -> user.getEmail().equals(email));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}