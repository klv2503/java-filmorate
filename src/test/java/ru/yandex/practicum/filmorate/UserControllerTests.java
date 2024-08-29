package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicateData;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

public class UserControllerTests {
    private UserController userController;
    //Стандартные корректные реквизиты юзера для тестов создания
    String standartEmail = "testemail@neverland.com";
    String standartLogin = "TestLogin";
    String standartName = "Anonim2024";
    LocalDate standartBirthday = LocalDate.of(1995, 1, 1);

    @BeforeEach
    public void setControllers() {
        userController = new UserController();
    }

    //Тесты создания нового экземпляра фильма
    @Test
    public void ifCreationNullUserThenException() {
        //Здесь проверяем, что происходит, если в контроллер передать пустой объект.
        //В дальнейшем при проверке конкретного поля остальные делаем корректными
        User user = new User();
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void ifCreationUserCorrectDataThenWithoutException() {
        //Добавление юзера с корректными данными
        User user = new User(standartEmail, standartLogin, standartName, standartBirthday);
        Assertions.assertDoesNotThrow(() -> userController.create(user));
    }

    //Пробуем различные варианты некорректного e-mail
    @Test
    public void ifCreationUserWithBlancEmailThenException() {
        User user = new User("", standartLogin, standartName, standartBirthday);
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void ifCreationUserWithNullEmailThenException() {
        User user = new User(null, standartLogin, standartName, standartBirthday);
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void ifCreationUserWithWrongEmailThenException() {
        User user = new User("wrongemail.com", standartLogin, standartName, standartBirthday);
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void ifCreationUserWithAnotherWrongEmailThenException() {
        User user = new User("wrong@email@error.com", standartLogin, standartName, standartBirthday);
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void ifCreationUserWithUsedEmailThenException() {
        //Создаем несколько пользователей, у одного из них есть e-mail, указанный у добавляемого
        DataForTests.generateUsers(userController);
        User user = new User("second@error.com", standartLogin, standartName, standartBirthday);
        Assertions.assertThrows(DuplicateData.class, () -> userController.create(user));
    }

    //Проверяем различные варианты некорректного login
    @Test
    public void ifCreationUserWithBlancLoginThenException() {
        User user = new User(standartEmail, "", standartName, standartBirthday);
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void ifCreationUserWithNullLoginThenException() {
        User user = new User(standartEmail, null, standartName, standartBirthday);
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void ifCreationUserWithLoginContainedBlancThenException() {
        User user = new User(standartEmail, "Test login", standartName, standartBirthday);
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void ifCreationUserWithUsedLoginThenException() {
        //Создаем несколько пользователей, у одного из них есть login, указанный у добавляемого
        DataForTests.generateUsers(userController);
        User user = new User(standartEmail, "thirdLogin", standartName, standartBirthday);
        Assertions.assertThrows(DuplicateData.class, () -> userController.create(user));
    }

    //Если name не задано, то в него заносится login
    @Test
    public void ifCreationUserWithEmptyNameThenNameEqualsLogin() {
        User user = new User(standartEmail, standartLogin, "", standartBirthday);
        User createdUser = userController.create(user);
        boolean isCorrectData = createdUser.getEmail().equals(user.getEmail())
                && createdUser.getLogin().equals(user.getLogin())
                && createdUser.getName().equals(user.getLogin())
                && createdUser.getBirthday().equals(user.getBirthday());
        Assertions.assertTrue(isCorrectData);
    }

    //Проверяем варианты некорректной даты рождения
    @Test
    public void ifCreationUserWithoutBirthdayThenException() {
        User user = new User(standartEmail, standartLogin, standartName, null);
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void ifCreationUserWithBirthdayAfterTodayThenException() {
        User user = new User(standartEmail, standartLogin, standartName, LocalDate.now().plusDays(1));
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void couldGiveUsersList() {
        //Добавляем четыре тестовых фильма и проверяем, все ли фильмы на месте и верно ли занесены данные
        DataForTests.generateUsers(userController);
        List<User> users = userController.findAll().stream().toList();
        int usersQuantity = users.size();
        //Должны получить два фильма с конкретными данными
        Assertions.assertEquals(4, usersQuantity);
        Assertions.assertEquals(1, users.get(0).getId());
        Assertions.assertEquals(2, users.get(1).getId());

        Assertions.assertEquals("first@error.com", users.get(0).getEmail());
        Assertions.assertEquals("secondLogin", users.get(1).getLogin());
        Assertions.assertEquals(LocalDate.of(2005, 3, 25), users.get(2).getBirthday());
        Assertions.assertEquals("NameTest4", users.get(3).getName());
    }

    //Тестируем изменения данных о пользователях. Вначале в списко заносятся 4 тестовых юзера,
    // затем пробуем вносить в них изменения

    @Test
    public void ifUpdatingFilmWithoutIdThenException() {
        DataForTests.generateUsers(userController);
        //Данные стандартные, но нет id юзера
        User user = new User(standartEmail, standartLogin, standartName, standartBirthday);
        Assertions.assertThrows(NotFoundException.class, () -> userController.update(user));
    }

    //Задаем id фильма и пробуем ситуации некорректных данных.
    @Test
    public void ifUpdatingUserWithEmptyEmailThenNoChanges() {
        DataForTests.generateUsers(userController);
        //Если пытаемся дать фильму пустой e-mail,
        // изменений не происходит, возвращаем юзера с заданным id без изменений
        User user = new User(1, "", standartLogin, standartName, standartBirthday);
        User userAfterUpdate = userController.update(user);
        boolean isUpdated = userAfterUpdate.equals(user);
        Assertions.assertFalse(isUpdated);
    }

    @Test
    public void ifUpdatingUserWithNullEmailThenNoChanges() {
        DataForTests.generateUsers(userController);
        //Если пытаемся дать фильму пустой e-mail,
        // изменений не происходит, возвращаем юзера с заданным id без изменений
        User user = new User(1, null, standartLogin, standartName, standartBirthday);
        User userAfterUpdate = userController.update(user);
        boolean isUpdated = userAfterUpdate.equals(user);
        Assertions.assertFalse(isUpdated);
    }

    @Test
    public void ifUpdatingUserWithWrongEmailThenNoChanges() {
        DataForTests.generateUsers(userController);
        //Если пытаемся дать фильму некорректный e-mail,
        // изменений не происходит, возвращаем юзера с заданным id без изменений
        User user = new User(1, "abracadabra.user.net", standartLogin, standartName, standartBirthday);
        User userAfterUpdate = userController.update(user);
        boolean isUpdated = userAfterUpdate.equals(user);
        Assertions.assertFalse(isUpdated);
    }

    @Test
    public void ifUpdatingUserWithTwiceAtEmailThenNoChanges() {
        DataForTests.generateUsers(userController);
        //Если пытаемся дать фильму некорректный e-mail,
        // изменений не происходит, возвращаем юзера с заданным id без изменений
        User user = new User(1, "abracadabra@user@net", standartLogin, standartName, standartBirthday);
        User userAfterUpdate = userController.update(user);
        boolean isUpdated = userAfterUpdate.equals(user);
        Assertions.assertFalse(isUpdated);
    }

    @Test
    public void ifUpdatingUserWithUsedEmailThenException() {
        DataForTests.generateUsers(userController);
        //Если пытаемся дать фильму e-mail, который занят другим пользователем, получаем исключение
        User user = new User(3, "second@error.com", standartLogin, standartName, standartBirthday);
        Assertions.assertThrows(DuplicateData.class, () -> userController.create(user));
    }

    @Test
    public void ifUpdatingUserWithSelfUsedEmailThenException() {
        DataForTests.generateUsers(userController);
        //Если пытаемся дать пользователю e-mail, который занят им же, изменения должны происходить
        User user = new User(2, "second@error.com", standartLogin, standartName, standartBirthday);
        User userAfterUpdate = userController.update(user);
        boolean isUpdated = userAfterUpdate.equals(user);
        Assertions.assertTrue(isUpdated);
    }

    @Test
    public void ifUpdatingUserWithNullBirthdayThenOldBirthday() {
        //Если корректны все данные, кроме дня рождения, данные меняем на новые,
        // а день рождения сохраняем установленный ранее
        DataForTests.generateUsers(userController);
        final LocalDate lockDate;
        lockDate = userController.findAll().stream().toList().get(0).getBirthday();
        User user = new User(1, standartEmail, standartLogin, standartName, null);
        User userAfterUpdate = userController.update(user);
        boolean isCorrectData = userAfterUpdate.getId() == 1
                && userAfterUpdate.getLogin().equals(standartLogin)
                && userAfterUpdate.getEmail().equals(standartEmail)
                && userAfterUpdate.getBirthday().equals(lockDate);
        Assertions.assertTrue(isCorrectData);
    }

    @Test
    public void ifUpdatingFilmWithWrongBirthdayThenOldBirthday() {
        //Пробуем среди корректных данных указать некорректную дату релиза
        DataForTests.generateUsers(userController);
        final LocalDate lockDate = userController.findAll().stream().toList().get(1).getBirthday();
        User user = new User(2, standartEmail, standartLogin, standartName, LocalDate.now().plusDays(1));
        User userAfterUpdate = userController.update(user);
        boolean isCorrectData = userAfterUpdate.getId() == 2
                && userAfterUpdate.getLogin().equals(standartLogin)
                && userAfterUpdate.getEmail().equals(standartEmail)
                && userAfterUpdate.getBirthday().equals(lockDate);
        Assertions.assertTrue(isCorrectData);
    }

    @Test
    public void ifUpdatingUserWithoutLoginThenNoChanges() {
        //Пытаемся изменить данные о пользователе, не указывая логин
        DataForTests.generateUsers(userController);
        User user = new User(3, standartEmail, null, standartName, standartBirthday);
        User userAfterUpdate = userController.update(user);
        boolean isUpdated = userAfterUpdate.equals(user);
        Assertions.assertFalse(isUpdated);
    }

    @Test
    public void ifUpdatingUserWithWrongLoginThenNoChanges() {
        //Пытаемся изменить данные о пользователе, указывая некорректный логин
        DataForTests.generateUsers(userController);
        User user = new User(4, standartEmail, "Test errorlogin", standartName, standartBirthday);
        User userAfterUpdate = userController.update(user);
        boolean isUpdated = userAfterUpdate.equals(user);
        Assertions.assertFalse(isUpdated);
    }

    @Test
    public void ifUpdatingUserWithUsedLoginThenNoChanges() {
        //Пытаемся изменить данные о пользователе, указывая новый логин, который занят другим пользователем
        DataForTests.generateUsers(userController);
        User user = new User(4, standartEmail, "firstLogin", standartName, standartBirthday);
        Assertions.assertThrows(DuplicateData.class, () -> userController.update(user));
    }

    @Test
    public void ifUpdatingWithWrongFilmIdThenException() {
        DataForTests.generateUsers(userController);
        User user = new User(10, standartEmail, standartLogin, standartName, standartBirthday);
        Assertions.assertThrows(NotFoundException.class, () -> userController.update(user));
    }
}
