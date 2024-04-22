package ru.yandex.practicum.filmorate.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmControllerTest {
    private static final String ENDPOINT_PATH = "/films";
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void should_return_all_films() throws Exception {
        mvc.perform(get(ENDPOINT_PATH))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void create_Ok() throws Exception {
        LocalDate now = LocalDate.now();
        final CreateFilmDto createFilmDto = new CreateFilmDto(1L,
                "Test name create ok",
                "Test description create ok",
                now,
                60);

       createFilm(createFilmDto)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Test name create ok"),
                        jsonPath("$.description").value("Test description create ok"),
                        jsonPath("$.releaseDate").value(now.toString()),
                        jsonPath("$.duration").value(60)
                );
    }

    @Test
    void create_BadRequest_incorrect_Fields() throws Exception {
        final CreateFilmDto incorrectFieldsFilmDto = new CreateFilmDto(1L,
                "",
                "Test description".repeat(50), // description > 200 chars
                LocalDate.of(1895, 12, 27),
                -2);


        createFilm(incorrectFieldsFilmDto)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_Conflict_Duplicate_Name() throws Exception {
        final CreateFilmDto twoCreateFilmDto = new CreateFilmDto(2L,
                "Test two",
                "Test description",
                LocalDate.of(1999, 12, 1),
                100);

        final CreateFilmDto duplicateNameTwoCreateFilmDto = new CreateFilmDto(3L,
                "Test two",
                "Test description",
                LocalDate.of(2000, 12, 1),
                120);

        createFilm(twoCreateFilmDto);

        createFilm(duplicateNameTwoCreateFilmDto)
                .andDo(print())
                .andExpect(status().isConflict()); // duplicate name
    }

    private ResultActions createFilm(CreateFilmDto createFilm) throws Exception {
        return mvc.perform(post(ENDPOINT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createFilm)));
    }


    @Test
    void update_Ok() throws Exception {
        LocalDate now = LocalDate.now();
        final CreateFilmDto createFilmDto = new CreateFilmDto(1L,
                "Test name create ok",
                "Test description create ok",
                now,
                60);

        createFilm(createFilmDto);

        LocalDate newReleaseDate = LocalDate.of(2000, 1, 12);

        final UpdateFilmDto updateFilmDto = new UpdateFilmDto(1L,
                "Test new name",
                "Test new description",
                newReleaseDate,
                120);

        updateFilm(updateFilmDto)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Test new name"),
                        jsonPath("$.description").value("Test new description"),
                        jsonPath("$.releaseDate").value(newReleaseDate.toString()),
                        jsonPath("$.duration").value(120));
    }

    private ResultActions updateFilm(UpdateFilmDto updateFilmDto) throws Exception {
        return mvc.perform(put(ENDPOINT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateFilmDto)));
    }

    @Test
    void update_NotFound_By_id() throws Exception {
        LocalDate now = LocalDate.now();
        final CreateFilmDto createFilmDto = new CreateFilmDto(1L,
                "Test name create ok",
                "Test description create ok",
                now,
                60);

        createFilm(createFilmDto);

        LocalDate newReleaseDate = LocalDate.of(2000, 1, 12);
        final UpdateFilmDto updateFilmDto = new UpdateFilmDto(Long.MAX_VALUE, // no id
                "Test new name",
                "Test new description",
                newReleaseDate,
                120);

        updateFilm(updateFilmDto)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void update_Conflict_Duplicate_Field_Name() throws Exception {
        LocalDate now = LocalDate.now();
        final CreateFilmDto createFilmDto = new CreateFilmDto(1L,
                "Test name create ok",
                "Test description create ok",
                now,
                60);

        createFilm(createFilmDto);

        final CreateFilmDto twoCreateFilmDto = new CreateFilmDto(2L,
                "Test name create ok two",
                "Test description create ok two",
                now,
                100);

        createFilm(twoCreateFilmDto);

        LocalDate newReleaseDate = LocalDate.of(2000, 1, 12);
        final UpdateFilmDto updateFilmDtoDuplicateName = new UpdateFilmDto(1L, // no id
                "Test name create ok two",
                "Test new description duplicate name",
                newReleaseDate,
                200);

        updateFilm(updateFilmDtoDuplicateName)
                .andDo(print())
                .andExpect(status().isConflict());
    }
}
