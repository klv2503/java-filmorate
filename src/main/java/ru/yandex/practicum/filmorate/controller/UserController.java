package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Измененные в связи с добавлением UserService методы
    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    public @ResponseBody User create(@Valid @RequestBody User user) {
        log.info("\nCreation user {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public @ResponseBody User update(@RequestBody User renewedUser) {
        log.info("\nUpdating user {}", renewedUser);
        if (renewedUser.getId() == null) {
            log.warn("\nNot updated {}", renewedUser);
            throw new NotFoundException("Id пользователя должен быть указан" + renewedUser, renewedUser);
        }
        return userService.changeUsersData(renewedUser);
    }

    //Добавленные методы
    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addFriends(@PathVariable String id, @PathVariable String friendId) {
        log.info("\nMaking {} as friend {}", id, friendId);
        long firstNum;
        long secondNum;
        if (isPositiveNumber(id)) {
            firstNum = Long.parseLong(id);
        } else {
            throw new ValidationException("Параметром id должно быть положительное число. Введено: " + id, id);
        }
        if (isPositiveNumber(friendId)) {
            secondNum = Long.parseLong(friendId);
        } else {
            throw new ValidationException("Параметром userId должно быть положительное число. Введено: " + friendId,
                    friendId);
        }
        if (firstNum == secondNum) {
            log.warn("\nNot added friends {} and {} because identifiers are equal", firstNum, secondNum);
            throw new ValidationException("Friends are not added.", "Identifiers have not be equal.");
        }
        return userService.makeNewFriendsPair(firstNum, secondNum);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> killFriendship(@PathVariable String id, @PathVariable String friendId) {
        log.info("\nDelete {} as friend {}", id, friendId);
        long firstNum;
        long secondNum;
        if (isPositiveNumber(id)) {
            firstNum = Long.parseLong(id);
        } else {
            throw new ValidationException("Параметром id должно быть положительное число. Введено: " + id, id);
        }
        if (isPositiveNumber(friendId)) {
            secondNum = Long.parseLong(friendId);
        } else {
            throw new ValidationException("Параметром friendId должно быть положительное число. Введено: " + friendId,
                    friendId);
        }
        if (firstNum == secondNum) {
            log.warn("\nNot deleted friendship between {} and {} because identifiers are equal", firstNum, secondNum);
            throw new ValidationException("Friends are not deleted.", "Identifiers have not be equal.");
        }
        return userService.deleteFromFriends(firstNum, secondNum);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUsersFriends(@PathVariable String id) {
        log.info("\nGetting friendslist of {}", id);
        if (isPositiveNumber(id)) {
            long usersNum = Long.parseLong(id);
            return userService.getUsersFriends(usersNum);
        } else {
            throw new ValidationException("Параметром id должно быть положительное число. Введено: " + id, id);
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getListCommonFriends(@PathVariable String id, @PathVariable String otherId) {
        log.info("\nGetting common friends {} and {}", id, otherId);
        long firstNum;
        long secondNum;
        if (isPositiveNumber(id)) {
            firstNum = Long.parseLong(id);
        } else {
            throw new ValidationException("Параметром id должно быть положительное число. Введено: " + id, id);
        }
        if (isPositiveNumber(otherId)) {
            secondNum = Long.parseLong(otherId);
        } else {
            throw new ValidationException("Параметром otherId должно быть положительное число. Введено: " + otherId,
                    otherId);
        }
        if (firstNum == secondNum) {
            log.warn("\nNot deleted friendship between {} and {} because identifiers are equal", firstNum, secondNum);
            throw new ValidationException("Friends are not deleted.", "Identifiers have not be equal.");
        }
        return userService.getCommonFriends(firstNum, secondNum);
    }

    public boolean isPositiveNumber(String testedString) {
        long num2Test;
        try {
            num2Test = Long.parseLong(testedString);
            if (num2Test <= 0) {
                return false;
            }
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

}