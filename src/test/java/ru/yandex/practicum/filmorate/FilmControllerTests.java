package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class FilmControllerTests {

    private FilmController filmController;
    //Стандартные корректные реквизиты фильма для тестов создания
    String standartName = "Name";
    String standartDescription = "Description";
    LocalDate standartReleaseDate = LocalDate.of(1995, 1, 1);
    Long standartDuration = 90L;

    @BeforeEach
    public void setControllers() {
        filmController = new FilmController();
    }

    //Тесты создания нового экземпляра фильма
    @Test
    public void ifCreationNullFilmThenException() {
        //Здесь проверяем, что происходит, если в контроллер передать пустой объект.
        //В дальнейшем при проверке конкретного поля остальные делаем корректными
        Film film = new Film();
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void ifCreationEmptyNamedFilmThenException() {
        Film film = new Film("", standartDescription, standartReleaseDate, standartDuration);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void ifCreationFilmWithTooLongDescriptionThenException() {
        Film film = new Film(standartName, "x".repeat(201), standartReleaseDate, standartDuration);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void ifCreationFilmWithoutReleaseDateThenException() {
        Film film = new Film();
        film.setName(standartName);
        film.setDescription(standartDescription);
        film.setDuration(standartDuration);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void ifCreationFilmWithReleaseDateEarlierThanCinemaBirthdayThenException() {
        Film film = new Film(standartName, standartDescription, LocalDate.of(1895, 12, 27), standartDuration);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void ifCreationFilmWithNotPositiveDurationThenException() {
        Film film = new Film(standartName, standartDescription, standartReleaseDate, 0L);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
        film.setDuration(-1L);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void ifCreationFilmCorrectDataThenWithoutException() {
        Film film = new Film(standartName, standartDescription, standartReleaseDate, standartDuration);
        Assertions.assertDoesNotThrow(() -> filmController.create(film));
    }

    @Test
    public void couldGiveFilmsList() {
        //Добавляем четыре тестовых фильма и проверяем, верно ли занесены данные
        DataForTests.generateFilms(filmController);
        List<Film> films = filmController.findAll().stream().toList();
        int filmsQuantity = films.size();
        //Должны получить два фильма с конкретными данными
        Assertions.assertEquals(4, filmsQuantity);
        Assertions.assertEquals(1, films.get(0).getId());
        Assertions.assertEquals(2, films.get(1).getId());

        Assertions.assertEquals("TestName1", films.get(0).getName());
        Assertions.assertEquals("TestDescr2", films.get(1).getDescription());
        Assertions.assertEquals(LocalDate.of(2000, 10, 10), films.get(0).getReleaseDate());
        Assertions.assertEquals(120L, films.get(1).getDuration());
    }

    //Тестируем изменения данных о фильмах. Вначале в списко заносятся 4 тестовых фильма,
    // затем пробуем вносить в них изменения

    @Test
    public void ifUpdatingFilmWithoutIdThenException() {
        DataForTests.generateFilms(filmController);
        //Данные стандартные, но нет id фильма
        Film film = new Film(standartName, standartDescription, standartReleaseDate, standartDuration);
        Assertions.assertThrows(NotFoundException.class, () -> filmController.update(film));
    }

    @Test
    public void ifUpdatingFilmWithWrongNameThenNoChanges() {
        DataForTests.generateFilms(filmController);
        //Задаем id фильма и пробуем ситуации неполноты данных.
        //Если пытаемся дать фильму пустое имя, изменений не происходит, возвращаем фильм с заданным id без изменений
        Film filmForUpdate = new Film(1, "", standartDescription, standartReleaseDate, standartDuration);
        Film filmAfterUpdate = filmController.update(filmForUpdate);
        boolean isUpdated = filmAfterUpdate.equals(filmForUpdate);
        Assertions.assertFalse(isUpdated);
    }

    @Test
    public void ifUpdatingFilmWithNullReleaseDateThenOldReleaseDate() {
        //Если задано непустое новое наименование и корректный id, меняем все данные,
        // которые корректно введены пользователем
        DataForTests.generateFilms(filmController);
        LocalDate lockDate = filmController.findAll().stream().toList().get(0).getReleaseDate();
        Film filmForUpdate = new Film(1, standartName, standartDescription, null, standartDuration);
        Film filmAfterUpdate = filmController.update(filmForUpdate);
        boolean isCorrectData = filmAfterUpdate.getId() == 1
                && filmAfterUpdate.getName().equals(standartName)
                && filmAfterUpdate.getDescription().equals(standartDescription)
                && filmAfterUpdate.getReleaseDate().equals(lockDate)
                && filmAfterUpdate.getDuration().equals(standartDuration);
        Assertions.assertTrue(isCorrectData);
    }

    @Test
    public void ifUpdatingFilmWithWrongReleaseDateThenOldReleaseDate() {
        //Пробуем среди корректных данных указать некорректную дату релиза
        DataForTests.generateFilms(filmController);
        LocalDate lockDate = filmController.findAll().stream().toList().get(1).getReleaseDate();
        Film filmForUpdate = new Film(2, standartName, standartDescription, LocalDate.of(1895, 12, 27), standartDuration);
        Film filmAfterUpdate = filmController.update(filmForUpdate);
        boolean isCorrectData = filmAfterUpdate.getId() == 2
                && filmAfterUpdate.getName().equals(standartName)
                && filmAfterUpdate.getDescription().equals(standartDescription)
                && filmAfterUpdate.getReleaseDate().equals(lockDate)
                && filmAfterUpdate.getDuration().equals(standartDuration);
        Assertions.assertTrue(isCorrectData);
    }

    @Test
    public void ifUpdatingFilmWithNonPositiveDurationThenOldDuration() {
        //Ставим нулевую продолжительность фильма
        DataForTests.generateFilms(filmController);
        long duration = filmController.findAll().stream().toList().get(2).getDuration();
        Film filmForUpdate = new Film(3, standartName, standartDescription, standartReleaseDate, 0L);
        Film filmAfterUpdate = filmController.update(filmForUpdate);
        boolean isCorrectData = filmAfterUpdate.getId() == 3
                && filmAfterUpdate.getName().equals(standartName)
                && filmAfterUpdate.getDescription().equals(standartDescription)
                && filmAfterUpdate.getReleaseDate().equals(standartReleaseDate)
                && filmAfterUpdate.getDuration().equals(duration);
        Assertions.assertTrue(isCorrectData);
    }

    @Test
    public void ifUpdatingFilmWithTooLongDescriptionThenOldDuration() {
        //Пытаемся изменить описание на очень длинное
        DataForTests.generateFilms(filmController);
        String descr = filmController.findAll().stream().toList().get(3).getDescription();
        Film filmForUpdate = new Film(4, standartName, "x".repeat(201), standartReleaseDate, standartDuration);
        Film filmAfterUpdate = filmController.update(filmForUpdate);
        boolean isCorrectData = filmAfterUpdate.getId() == 4
                && filmAfterUpdate.getName().equals(standartName)
                && filmAfterUpdate.getDescription().equals(descr)
                && filmAfterUpdate.getReleaseDate().equals(standartReleaseDate)
                && filmAfterUpdate.getDuration().equals(standartDuration);
        Assertions.assertTrue(isCorrectData);
    }

    @Test
    public void ifUpdatingWithWrongFilmIdThenException() {
        DataForTests.generateFilms(filmController);
        Film film = new Film(10, standartName, standartDescription, standartReleaseDate, standartDuration);
        Assertions.assertThrows(NotFoundException.class, () -> filmController.update(film));
    }
}
