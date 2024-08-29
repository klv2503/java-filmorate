package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public @ResponseBody User create(@RequestBody User user) {
        // проверяем валидность логина
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Not created {}", user);
            throw new ValidationException("Логин пользователя не может быть пустым", user);
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Not created {}", user);
            throw new ValidationException("Логин пользователя не может содержать пробелы", user);
        }
        if (isUsedLogin(user.getLogin())) {
            log.warn("Not created {}", user);
            throw new DuplicateData("Этот login уже используется", user);
        }
// проверяем валидность e-mail
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Not created {}", user);
            throw new ValidationException("E-mail пользователя не может быть пустым", user);
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Not created {}", user);
            throw new ValidationException("E-mail пользователя должен содержать знак '@'", user);
        }
        if (user.getEmail().indexOf("@") != user.getEmail().lastIndexOf("@")) {
            log.warn("Not created {}", user);
            throw new ValidationException("E-mail пользователя должен ровно один знак '@'", user);
        }
        if (isUsedEmail(user.getEmail())) {
            log.warn("Not created {}", user);
            throw new DuplicateData("Этот e-mail уже используется", user);
        }
        // проверяем валидность даты рождения
        try {
            LocalDate usersDate = user.getBirthday();
            if (LocalDate.now().isBefore(usersDate)) {
                throw new RuntimeException("");
            }
        } catch (RuntimeException e) {
            log.warn("Not created {}", user);
            throw new ValidationException("Некорректные данные даты рождения", user);
        }
        // формируем дополнительные данные
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        log.info("Successfully created {}", user);
        return user;
    }

    @PutMapping
    public @ResponseBody User update(@RequestBody User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            log.warn("Not updated {}", newUser);
            throw new NotFoundException("Id должен быть указан", newUser);
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getLogin() == null || newUser.getLogin().isBlank()
                    || newUser.getLogin().contains(" ")) {
                log.warn("Not updated {}", newUser);
                return oldUser;
            }
            if (!newUser.getLogin().equals(oldUser.getLogin()) && isUsedLogin(newUser.getLogin())) {
                log.warn("Not updated {}", newUser);
                throw new DuplicateData("Этот login уже используется", newUser);
            }
            if (newUser.getEmail() == null || newUser.getEmail().isBlank()
                    || !newUser.getEmail().contains("@") ||
                    newUser.getEmail().indexOf("@") != newUser.getEmail().lastIndexOf("@")) {
                log.warn("Not updated {}", newUser);
                return oldUser;
            }
            if (!oldUser.getEmail().equals(newUser.getEmail()) && isUsedEmail(newUser.getEmail())) {
                log.warn("Not updated {}", newUser);
                throw new DuplicateData("Этот e-mail уже используется", newUser);
            }
            // проверяем валидность даты рождения
            if (newUser.getBirthday() != null) {
                try {
                    LocalDate usersDate = newUser.getBirthday();
                } catch (RuntimeException e) {
                    log.warn("Not updated {}", newUser);
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
            log.info("Successfully updated {}.", oldUser);
            return oldUser;
        }
        log.warn("Not updated {}", newUser);
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