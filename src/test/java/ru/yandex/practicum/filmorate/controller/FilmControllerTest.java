package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.EntityDuplicateException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.model.film.dto.*;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@RequiredArgsConstructor
class FilmControllerTest {
    private static final String ENDPOINT_PATH = "/films";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmStorage filmStorage;

    @MockBean(name = "userService")
    private UserStorage userStorage;

    @Qualifier("mvcConversionService")
    private final ConversionService cs;

    @Test
    void should_return_all_films() throws Exception {
        List<FilmDto> filmDtos = new ArrayList<>();

        LocalDate now = LocalDate.now();
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder().id(1L).build());
        MPA mpa = MPA.builder().id(1L).build();

        List<Director> directors = new ArrayList<>();
        directors.add(Director.builder().id(1L).name("Director's Name").build());

        for (int i = 0; i < 4; i++) {
            final FilmDto createFilmDto = new FilmDto((long) i,
                    "Test name create ok " + i,
                    "Test description create ok " + i,
                    now,
                    60 + i,
                    genres,
                    mpa,
                    directors);
            filmDtos.add(createFilmDto);
        }
        when(filmStorage.findAll())
                .thenReturn(filmDtos);

        mvc.perform(get(ENDPOINT_PATH))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void create_Ok() throws Exception {
        LocalDate now = LocalDate.now();
        Set<GenreIdDto> genreIdDtos = new HashSet<>();
        genreIdDtos.add(GenreIdDto.builder().id(1L).build());
        MPAIdDto mpaIdDto = MPAIdDto.builder().id(1L).build();
        Set<DirectorDto> directorDtos = new HashSet<>();
        directorDtos.add(DirectorDto.builder().id(1L).name("Director's Name").build());

        final CreateFilmDto createFilmDto = new CreateFilmDto(1L,
                "Test name create ok",
                "Test description create ok",
                now,
                60,
                genreIdDtos,
                mpaIdDto,
                directorDtos);

        Film convertFilm = cs.convert(createFilmDto, Film.class);
        FilmDto convert = cs.convert(convertFilm, FilmDto.class);

        when(filmStorage.create(createFilmDto))
                .thenReturn(convert);

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
        Set<GenreIdDto> genreIdDtos = new HashSet<>();
        genreIdDtos.add(GenreIdDto.builder().id(1L).build());
        MPAIdDto mpaIdDto = MPAIdDto.builder().id(1L).build();
        Set<DirectorDto> directorDtos = new HashSet<>();
        directorDtos.add(DirectorDto.builder().id(1L).name("Director's Name").build());

        final CreateFilmDto incorrectFieldsFilmDto = new CreateFilmDto(1L,
                "",
                "Test description".repeat(50), // description > 200 chars
                LocalDate.of(1895, 12, 27),
                -2,
                genreIdDtos,
                mpaIdDto,
                directorDtos);

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(filmStorage)
                .create(any());

        createFilm(incorrectFieldsFilmDto)
                .andDo(print())
                .andExpect(status()
                        .isBadRequest());
    }

    @Test
    void create_Conflict_Duplicate_Name() throws Exception {
        LocalDate now = LocalDate.now();
        Set<GenreIdDto> genreIdDtos = new HashSet<>();
        genreIdDtos.add(GenreIdDto.builder().id(1L).build());
        MPAIdDto mpaIdDto = MPAIdDto.builder().id(1L).build();
        Set<DirectorDto> directorDtos = new HashSet<>();
        directorDtos.add(DirectorDto.builder().id(1L).name("Director's Name").build());

        final CreateFilmDto createFilmDto = new CreateFilmDto(1L,
                "Test name create ok",
                "Test description create ok",
                now,
                60,
                genreIdDtos,
                mpaIdDto,
                directorDtos);

        doThrow(new EntityDuplicateException("field name", "name value"))
                .when(filmStorage)
                .create(any());

        createFilm(createFilmDto)
                .andDo(print())
                .andExpect(status()
                        .isConflict()); // duplicate name

    }

    private ResultActions createFilm(CreateFilmDto createFilm) throws Exception {
        return mvc.perform(post(ENDPOINT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createFilm)));
    }


    @Test
    void update_Ok() throws Exception {
        Set<GenreIdDto> genreIdDtos = new HashSet<>();
        genreIdDtos.add(GenreIdDto.builder().id(1L).build());
        MPAIdDto mpaIdDto = MPAIdDto.builder().id(1L).build();
        Set<DirectorDto> directorDtos = new HashSet<>();
        directorDtos.add(DirectorDto.builder().id(1L).name("Director's Name").build());

        LocalDate newReleaseDate = LocalDate.of(2000, 1, 12);

        final UpdateFilmDto updateFilmDto = new UpdateFilmDto(1L,
                "Test new name",
                "Test new description",
                newReleaseDate,
                120,
                genreIdDtos,
                mpaIdDto,
                directorDtos);

        Film convertFilm = cs.convert(updateFilmDto, Film.class);
        FilmDto filmDto = cs.convert(convertFilm, FilmDto.class);

        when(filmStorage.update(updateFilmDto))
                .thenReturn(filmDto);

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
        Set<GenreIdDto> genreIdDtos = new HashSet<>();
        genreIdDtos.add(GenreIdDto.builder().id(1L).build());
        MPAIdDto mpaIdDto = MPAIdDto.builder().id(1L).build();
        Set<DirectorDto> directorDtos = new HashSet<>();
        directorDtos.add(DirectorDto.builder().id(1L).name("Director's Name").build());

        LocalDate newReleaseDate = LocalDate.of(2000, 1, 12);

        final UpdateFilmDto updateFilmDto = new UpdateFilmDto(1L,
                "Test new name",
                "Test new description",
                newReleaseDate,
                120,
                genreIdDtos,
                mpaIdDto,
                directorDtos);

        doThrow(new EntityNotFoundByIdException("film", updateFilmDto.id().toString()))
                .when(filmStorage)
                .update(any());

        updateFilm(updateFilmDto)
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    void update_Conflict_Duplicate_Field_Name() throws Exception {
        Set<GenreIdDto> genreIdDtos = new HashSet<>();
        genreIdDtos.add(GenreIdDto.builder().id(1L).build());
        MPAIdDto mpaIdDto = MPAIdDto.builder().id(1L).build();
        Set<DirectorDto> directorDtos = new HashSet<>();
        directorDtos.add(DirectorDto.builder().id(1L).name("Director's Name").build());

        LocalDate newReleaseDate = LocalDate.of(2000, 1, 12);

        final UpdateFilmDto updateFilmDto = new UpdateFilmDto(1L,
                "Test new name",
                "Test new description",
                newReleaseDate,
                120,
                genreIdDtos,
                mpaIdDto,
                directorDtos);

        doThrow(new EntityDuplicateException("field name", "name value"))
                .when(filmStorage)
                .update(any());

        updateFilm(updateFilmDto)
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void addLikeFilm_ok() throws Exception {
        String testId = "123";
        String testIdTwo = "321";
        String pathAddLike = ENDPOINT_PATH + "/%s/like/%s"
                .formatted(testId, testIdTwo);

        mvc.perform(put(pathAddLike))
                .andDo(print())
                .andExpect(status().isOk());

    }

    private ObjForTest createAndGetObjForTest() {
        Set<GenreIdDto> genreIdDtos = new HashSet<>();
        genreIdDtos.add(GenreIdDto.builder().id(1L).build());
        MPAIdDto mpaIdDto = MPAIdDto.builder().id(1L).build();
        Set<DirectorDto> directorDtos = new HashSet<>();
        directorDtos.add(DirectorDto.builder().id(1L).name("Director's Name").build());
        LocalDate now = LocalDate.now();

        final CreateFilmDto createFilmDto = new CreateFilmDto(1L,
                "Test name create ok",
                "Test description create ok",
                now,
                60,
                genreIdDtos,
                mpaIdDto,
                directorDtos);

        FilmDto filmDto = filmStorage.create(createFilmDto);

        LocalDate birthday = LocalDate.now().minusYears(20);
        final CreateUserDto createUserDto = new CreateUserDto(1L,
                "ttt@aa.ru",
                "TestLogin",
                "Test name",
                birthday);

        UserDto userDto = userStorage.create(createUserDto);
        ObjForTest objects = new ObjForTest(filmDto, userDto);
        return objects;
    }

    private record ObjForTest(FilmDto filmDto, UserDto userDto) {
    }

    @Test
    void addLikeFilm_NotFound_Film() throws Exception {
        long notFoundIdFilm = 99L;
        long notFoundIdUser = 88L;

        doThrow(new EntityNotFoundByIdException("film", String.valueOf(notFoundIdFilm)))
                .when(filmStorage)
                .addLikeFilm(notFoundIdFilm, notFoundIdUser);

        String pathAddLike = ENDPOINT_PATH + "/%s/like/%s"
                .formatted(notFoundIdFilm, notFoundIdUser);

        mvc.perform(put(pathAddLike))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void addLikeFilm_NotFound_User() throws Exception {
        long notFoundIdFilm = 99L;
        long notFoundIdUser = 88L;

        doThrow(new EntityNotFoundByIdException("user", String.valueOf(notFoundIdFilm)))
                .when(filmStorage)
                .addLikeFilm(notFoundIdFilm, notFoundIdUser);

        String pathAddLike = ENDPOINT_PATH + "/%s/like/%s"
                .formatted(notFoundIdFilm, notFoundIdUser);

        mvc.perform(put(pathAddLike))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    void deleteLikeFilm_ok() throws Exception {
        String idTest = "123";
        String idTestTwo = "321";
        String pathAddLike = ENDPOINT_PATH + "/%s/like/%s"
                .formatted(idTest, idTestTwo);

        mvc.perform(delete(pathAddLike))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteLikeFilm_NotFound_Film() throws Exception {
        long notFoundIdFilm = 99L;
        long notFoundIdUser = 88L;

        doThrow(new EntityNotFoundByIdException("film", String.valueOf(notFoundIdFilm)))
                .when(filmStorage)
                .deleteLikeFilm(notFoundIdFilm, notFoundIdUser);

        String pathAddLike = ENDPOINT_PATH + "/%s/like/%s"
                .formatted(notFoundIdFilm, notFoundIdUser);

        mvc.perform(delete(pathAddLike))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteLikeFilm_NotFound_User() throws Exception {
        long notFoundIdFilm = 99L;
        long notFoundIdUser = 88L;

        doThrow(new EntityNotFoundByIdException("user", String.valueOf(notFoundIdFilm)))
                .when(filmStorage)
                .deleteLikeFilm(notFoundIdFilm, notFoundIdUser);

        String pathAddLike = ENDPOINT_PATH + "/%s/like/%s"
                .formatted(notFoundIdFilm, notFoundIdUser);

        mvc.perform(delete(pathAddLike))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void findPopularFilms() throws Exception {
        String count = "12";
        String pathFindPopularFilms = ENDPOINT_PATH + "/popular?count=%s".formatted(count);

        ObjForTest objectsForTest = createAndGetObjForTest();

        mvc.perform(get(pathFindPopularFilms))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
