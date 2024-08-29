package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    // строго говоря, первый публичный сеанс братья Люмьер провели 22.03.1895,
    // но задание есть задание
    private final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);
    private final int maxDescriptionLength = 200;
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public @ResponseBody Film create(@RequestBody Film film) {
        // проверяем валидность названия фильма
        if (film.getName() == null || film.getName().isBlank()) {
            log.info("Not created {}", film);
            throw new ValidationException("Название фильма не может быть пустым", film);
        }
        // проверяем валидность описания фильма
        if (film.getDescription().length() > maxDescriptionLength) {
            log.info("Not created {}", film);
            throw new ValidationException("Слишком длинное описание фильма (более 200 символов)", film);
        }
        // проверяем валидность даты релиза
        // (считаем, что допускается дата в будущем - фильм еще не вышел, но дата релиза уже объявлена)
        try {
            LocalDate filmDate = film.getReleaseDate();
            if (filmDate.isBefore(cinemaBirthday)) {
                throw new RuntimeException("");
            }
        } catch (RuntimeException e) {
            log.info("Not created {}", film);
            throw new ValidationException("Некорректные данные даты релиза", film);
        }
        //Проверяем, что продолжительность фильма положительна
        if (film.getDuration() <= 0) {
            log.info("Not created {}", film);
            throw new ValidationException("Продолжительность фильма должна быть положительным числом", film);
        }
        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новый фильм в памяти приложения
        films.put(film.getId(), film);
        log.info("Successfully created {}", film);
        return film;
    }

    @PutMapping
    public @ResponseBody Film update(@RequestBody Film newFilm) {
        // проверяем необходимые условия
        if (newFilm.getId() == null) {
            log.info("Not updated {}", newFilm);
            throw new NotFoundException("Id должен быть указан", newFilm);
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                log.info("Not updated {}", newFilm);
                return oldFilm;
            }
            // проверяем валидность даты релиза
            if (newFilm.getReleaseDate() != null) {
                try {
                    LocalDate filmDate = newFilm.getReleaseDate();
                } catch (RuntimeException e) {
                    log.info("Not updated {}", newFilm);
                    return oldFilm;
                }
            }
            // если фильм найден и данные валидны, обновляем данные
            oldFilm.setName(newFilm.getName());
            if (newFilm.getDescription().length() <= maxDescriptionLength)
                oldFilm.setDescription(newFilm.getDescription());
            if (newFilm.getReleaseDate() != null)
                if (!newFilm.getReleaseDate().isBefore(cinemaBirthday)) {
                    oldFilm.setReleaseDate(newFilm.getReleaseDate());
                }
            if (newFilm.getDuration() > 0)
                oldFilm.setDuration(newFilm.getDuration());
            log.info("Successfully updated {}", oldFilm);
            return oldFilm;
        }
        log.info("Not updated {}", newFilm);
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден", newFilm);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
