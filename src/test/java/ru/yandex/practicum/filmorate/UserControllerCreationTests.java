package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicateData;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerCreationTests {
    private final Gson gson = DataForTests.getGson();

    User goodUser = new User("without@error.com", "correctLogin", "CorrectName",
            LocalDate.of(2000, 10, 10));

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void ifCreationCorrectUserThenOk() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(goodUser))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void ifCreationWithoutBodyThenBadRequest() throws Exception {
        //Здесь выдается исключение HttpMessageNotReadableException, которое я пока не обрабатываю
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .content("")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationNullLoginUserThenBadRequest() throws Exception {
        User newUser = new User();
        newUser = goodUser;
        newUser.setLogin(null);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newUser))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationEmptyLoginUserThenBadRequest() throws Exception {
        User newUser = new User();
        newUser = goodUser;
        newUser.setLogin("");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newUser))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationLoginWithSpacesThenBadRequest() throws Exception {
        User newUser = new User();
        newUser = goodUser;
        newUser.setLogin("Bad users login");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newUser))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationEmptyEmailThenBadRequest() throws Exception {
        User newUser = new User();
        newUser = goodUser;
        newUser.setEmail("");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newUser))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationInvalidEmailThenBadRequest() throws Exception {
        User newUser = new User();
        newUser = goodUser;
        newUser.setEmail("bad@users@.email.net");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newUser))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationNullEmailThenBadRequest() throws Exception {
        User newUser = new User();
        newUser = goodUser;
        newUser.setEmail(null);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newUser))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationNullBirthdayThenBadRequest() throws Exception {
        User newUser = new User();
        newUser = goodUser;
        newUser.setBirthday(null);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newUser))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationWithBirthdayAfterTodayThenBadRequest() throws Exception {
        //Здесь исключение выдает Json, я его ещё не научился обрабатывать так, чтобы выдать объект,
        // на котором сгенерировано это исключение
        User newUser = new User();
        newUser = goodUser;
        newUser.setBirthday(LocalDate.now().plusDays(1));
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newUser))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    //следующие три теста не требуют вмешательства MockMvc, но из соображений общности они перенесены в этот класс,
    // поскольку относятся к созданию
    @Test
    public void ifCreationUserWithUsedLoginThenException() {
        //Создаем несколько пользователей, у одного из них есть login, указанный у добавляемого
        UserController userController = new UserController();
        DataForTests.generateUsers(userController);
        User user = new User(goodUser.getEmail(), "thirdLogin", goodUser.getName(), goodUser.getBirthday());
        Assertions.assertThrows(DuplicateData.class, () -> userController.create(user));
    }

    @Test
    public void ifCreationUserWithUsedEmailThenException() {
        //Создаем несколько пользователей, у одного из них есть e-mail, указанный у добавляемого
        UserController userController = new UserController();
        DataForTests.generateUsers(userController);
        User user = new User("second@error.com", goodUser.getLogin(), goodUser.getName(), goodUser.getBirthday());
        Assertions.assertThrows(DuplicateData.class, () -> userController.create(user));
    }

    //Если name не задано, то в него заносится login
    @Test
    public void ifCreationUserWithEmptyNameThenNameEqualsLogin() {
        UserController userController = new UserController();
        User user = new User(goodUser.getEmail(), goodUser.getLogin(), "", goodUser.getBirthday());
        User createdUser = userController.create(user);
        boolean isCorrectData = createdUser.getEmail().equals(user.getEmail())
                && createdUser.getLogin().equals(user.getLogin())
                && createdUser.getName().equals(user.getLogin())
                && createdUser.getBirthday().equals(user.getBirthday());
        Assertions.assertTrue(isCorrectData);
    }
}
