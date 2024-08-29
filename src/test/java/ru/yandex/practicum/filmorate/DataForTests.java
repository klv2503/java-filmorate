package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class DataForTests {

    public static void generateFilms(FilmController filmController) {
        filmController.create(new Film("TestName1", "TestDescr1", LocalDate.of(2000, 10, 10), 100L));
        filmController.create(new Film("TestName2", "TestDescr2", LocalDate.of(2010, 5, 5), 120L));
        filmController.create(new Film("TestName3", "TestDescr3", LocalDate.of(2005, 3, 25), 80L));
        filmController.create(new Film("TestName4", "TestDescr4", LocalDate.of(1998, 8, 31), 70L));
    }

    public static void generateUsers(UserController userController) {
        userController.create(new User("first@error.com", "firstLogin", "NameTest1", LocalDate.of(2000, 10, 10)));
        userController.create(new User("second@error.com", "secondLogin", "NameTest2", LocalDate.of(2010, 5, 5)));
        userController.create(new User("third@error.com", "thirdLogin", "NameTest3", LocalDate.of(2005, 3, 25)));
        userController.create(new User("forth@error.com","forthLogin", "NameTest4", LocalDate.of(1998, 8, 31)));
    }
}
