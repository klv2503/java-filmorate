package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/popular")
    public Collection<Film> findFilms(@RequestParam(required = false, defaultValue = "10") String count) {
        log.info("\nGetting {} most popular films", count);
        //Решил сразу контролировать корректность параметра.
        //Здесь и далее числа принимаются в формате строки с тем, чтобы устроить защиту от дурака (скорее, вредителя)
        int howFilms;
        if (isPositiveNumber(count)) {
            howFilms = Integer.parseInt(count);
        } else
            throw new ValidationException("Параметром должно быть положительное число. Введено: " + count,
                    "Параметром должно быть положительное число. Введено: " + count);
        return filmService.getPopularFilms(howFilms);
    }

    @PutMapping("/{id}/like/{userId}")
    public List<User> addUsersLike(@PathVariable String id, @PathVariable String userId) {
        log.info("\nAdding of like to film {} from user {}", id, userId);
        long filmNum;
        long userNum;
        if (isPositiveNumber(id)) {
            filmNum = Long.parseLong(id);
        } else {
            throw new ValidationException("Параметром id должно быть положительное число. Введено: " + id,
                    "Параметром id должно быть положительное число. Введено: " + id);
        }
        if (isPositiveNumber(userId)) {
            userNum = Long.parseLong(userId);
        } else {
            throw new ValidationException("Параметром userId должно быть положительное число. Введено: " + userId,
                    "Параметром userId должно быть положительное число. Введено: " + userId);
        }
        return filmService.addUsersLike(filmNum, userNum);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public List<User> deleteUsersLike(@PathVariable String id, @PathVariable String userId) {
        log.info("\nDeleting of like to film {} from user {}", id, userId);
        long filmNum;
        long userNum;
        if (isPositiveNumber(id)) {
            filmNum = Long.parseLong(id);
        } else {
            throw new ValidationException("Параметром id должно быть положительное число. Введено: " + id,
                    "Параметром id должно быть положительное число. Введено: " + id);
        }
        if (isPositiveNumber(userId)) {
            userNum = Long.parseLong(userId);
        } else {
            throw new ValidationException("Параметром userId должно быть положительное число. Введено: " + userId,
                    "Параметром userId должно быть положительное число. Введено: " + userId);
        }
        return filmService.deleteUsersLike(filmNum, userNum);
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.getAll();
    }

    @PostMapping
    public @ResponseBody Film create(@Valid @RequestBody Film film) {
        log.info("\nCreation of {}", film);
        film = filmService.addNewFilm(film);
        log.info("\nSuccessfully created {}", film);
        return film;
    }

    @PutMapping
    public @ResponseBody Film update(@RequestBody Film newFilm) {
        // проверяем необходимые условия
        log.info("\nUpdating of {}", newFilm);
        if (newFilm.getId() == null) {
            log.info("\nNot updated {}", newFilm);
            throw new NotFoundException("Id фильма должен быть указан: " + newFilm, newFilm);
        }
        return filmService.modifyFilm(newFilm);
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam String id) {
        log.info("\nDeleting of film id={}", id);
        long filmId;
        if (isPositiveNumber(id)) {
            filmId = Long.parseLong(id);
        } else {
            throw new ValidationException("Id фильма должен быть положительным целым числом. Введено: " + id, id);
        }
        filmService.deleteFilm(filmId);
        log.info("\nSuccessfully deleted {}", filmId);
        return new ResponseEntity<>("Successfully deleted film: id=" + filmId, HttpStatus.OK);
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
