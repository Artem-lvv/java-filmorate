package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.GenreIdDto;
import ru.yandex.practicum.filmorate.model.film.dto.MPAIdDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.FilmRepository;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.UserRepository;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@JdbcTest
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {FilmRepository.class,
        FilmRowMapper.class,
        UserRepository.class,
        UserRowMapper.class})
public class FilmRepositoryTest {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void create_ok() {
        ObjForTest objForTest = createAndGetObjForTest();
        Long id = filmRepository.create(objForTest.createFilmDto);

        Optional<Film> filmOptional = filmRepository.findById(id);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                            assertThat(film).hasFieldOrPropertyWithValue("id",
                                    id);
                            assertThat(film).hasFieldOrPropertyWithValue("name",
                                    objForTest.createFilmDto().name());
                            assertThat(film).hasFieldOrPropertyWithValue("description",
                                    objForTest.createFilmDto().description());
                            assertThat(film).hasFieldOrPropertyWithValue("releaseDate",
                                    objForTest.createFilmDto().releaseDate());
                            assertThat(film).hasFieldOrPropertyWithValue("duration",
                                    objForTest.createFilmDto().duration());
                        }
                );
    }

    @Test
    @Sql("/sql/insert-data.sql")
    void update_ok() {
        long id = 1L;

        Set<GenreIdDto> genreIdDtos = new HashSet<>();
        genreIdDtos.add(GenreIdDto.builder().id(1L).build());
        MPAIdDto mpaIdDto = MPAIdDto.builder().id(1L).build();
        LocalDate now = LocalDate.now();

        final UpdateFilmDto updateFilmDto = new UpdateFilmDto(id,
                "Test name create ok update",
                "Test description create ok update",
                now,
                60,
                genreIdDtos,
                mpaIdDto);

        Optional<Film> filmUpdate = filmRepository.findById(id);

        assertThat(filmUpdate)
                .isPresent()
                .hasValueSatisfying(film -> {
                            assertThat(film).hasFieldOrPropertyWithValue("id",
                                    id);
                            assertThat(film).hasFieldOrPropertyWithValue("name",
                                    filmUpdate.get().getName());
                            assertThat(film).hasFieldOrPropertyWithValue("description",
                                    filmUpdate.get().getDescription());
                            assertThat(film).hasFieldOrPropertyWithValue("releaseDate",
                                    filmUpdate.get().getReleaseDate());
                            assertThat(film).hasFieldOrPropertyWithValue("duration",
                                    filmUpdate.get().getDuration());
                        }
                );
    }

    @Test
    void findAll_ok() {
        ObjForTest objForTest = createAndGetObjForTest();
        filmRepository.create(objForTest.createFilmDto);
        filmRepository.create(objForTest.createFilmDtoTwo);

        List<Film> all = filmRepository.findAll();

        assertThat(all).isNotEmpty();
    }

    @Test
    void findById_ok() {
        ObjForTest objForTest = createAndGetObjForTest();
        Long filmId = filmRepository.create(objForTest.createFilmDto);

        Optional<Film> filmByid = filmRepository.findById(filmId);

        assertThat(filmByid)
                .isPresent()
                .hasValueSatisfying(film -> {
                            assertThat(film).hasFieldOrPropertyWithValue("id",
                                    filmId);
                            assertThat(film).hasFieldOrPropertyWithValue("name",
                                    objForTest.createFilmDto.name());
                            assertThat(film).hasFieldOrPropertyWithValue("description",
                                    objForTest.createFilmDto.description());
                            assertThat(film).hasFieldOrPropertyWithValue("releaseDate",
                                    objForTest.createFilmDto.releaseDate());
                            assertThat(film).hasFieldOrPropertyWithValue("duration",
                                    objForTest.createFilmDto.duration());
                        }
                );
    }

    private ObjForTest createAndGetObjForTest() {
        Set<GenreIdDto> genreIdDtos = new HashSet<>();
        genreIdDtos.add(GenreIdDto.builder().id(1L).build());
        MPAIdDto mpaIdDto = MPAIdDto.builder().id(1L).build();
        LocalDate now = LocalDate.now();

        final CreateFilmDto createFilmDto = new CreateFilmDto(1L,
                "Test name create ok",
                "Test description create ok",
                now,
                60,
                genreIdDtos,
                mpaIdDto);

        Set<GenreIdDto> genreIdDtosTwo = new HashSet<>();
        genreIdDtos.add(GenreIdDto.builder().id(1L).build());
        MPAIdDto mpaIdDtoTwo = MPAIdDto.builder().id(1L).build();
        LocalDate nowTwo = LocalDate.now();

        final CreateFilmDto createFilmDtoTwo = new CreateFilmDto(2L,
                "Test name create ok two",
                "Test description create ok two",
                nowTwo,
                70,
                genreIdDtosTwo,
                mpaIdDtoTwo);

        return new ObjForTest(createFilmDto, createFilmDtoTwo);
    }

    private record ObjForTest(CreateFilmDto createFilmDto, CreateFilmDto createFilmDtoTwo) {
    }

}
