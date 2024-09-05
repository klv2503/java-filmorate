package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerCreationTests {

    private final Gson gson = DataForTests.getGson();

    Film errorDateFilm = new Film("ErrorTestName", "ErrorTestDescr",
            LocalDate.of(1895, 10, 10), 100L);
    Film goodFilm = new Film("GoodTestName", "GoodTestDescr",
            LocalDate.of(2010, 5, 5), 120L);

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void ifCreationCorrectFilmThenOk() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/films")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(goodFilm))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void ifCreationWithoutBodyThenBadRequest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/films")
                .accept(MediaType.APPLICATION_JSON)
                .content("")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationEmptyNamedFilmThenBadRequest() throws Exception {
        Film newFilm = new Film();
        newFilm = goodFilm;
        newFilm.setName("");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/films")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newFilm))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationFilmWithNonPositiveDurationThenBadRequest() throws Exception {
        //Negative duration
        Film newFilm = new Film();
        newFilm = goodFilm;
        newFilm.setDuration(-90L);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/films")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newFilm))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
        //Duration equals zero
        newFilm.setDuration(0L);
        request = MockMvcRequestBuilders
                .post("/films")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newFilm))
                .contentType(MediaType.APPLICATION_JSON);

        result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationFilmWithOnlyNameThenBadRequest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/films")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Name\"}")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationFilmWithOnlyDescriptionThenBadRequest() throws Exception {
        //примечание - поскольку ошибки в нескольких полях, выдается несколько сообщений об ошибках
        RequestBuilder request = MockMvcRequestBuilders
                .post("/films")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Name\"}")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationFilmWithTooLongDescriptionThenBadRequest() throws Exception {
        Film newFilm = new Film();
        newFilm = goodFilm;
        newFilm.setDescription("x".repeat(201));
        RequestBuilder request = MockMvcRequestBuilders
                .post("/films")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newFilm))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void ifCreationFilmWithIllegalReleaseDateThenBadRequest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/films")
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(errorDateFilm))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isBadRequest()).andReturn();
    }

}
