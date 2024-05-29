package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.inDataBase.FilmDBStorage;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest2 {
    private static final String ENDPOINT_PATH = "/films";
    @Autowired
    private MockMvc mvc;

    @MockBean(name = "filmDBStorage")
    private FilmDBStorage filmDBStorage;

    @MockBean
    private FilmService filmService;

    @Test
    void should_return_all_films() throws Exception {
        // given
        when(filmDBStorage.findAll())
                .thenReturn(
                        List.of(
                                FilmDto.builder().name("test0").build(),
                                FilmDto.builder().name("test1").build(),
                                FilmDto.builder().name("test2").build(),
                                FilmDto.builder().name("test3").build()
                        )
                );

        // when
        mvc.perform(get(ENDPOINT_PATH))
                .andDo(print())
                // then
                .andExpect(status().isOk());
    }
}
