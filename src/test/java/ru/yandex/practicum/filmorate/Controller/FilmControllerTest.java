package ru.yandex.practicum.filmorate.Controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.service.MappingDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    FilmController filmController;

    @BeforeEach
    void beforeEach() {
       filmController = new FilmController();
    }

    @Test
    void findAll() {
        final Film film = Film.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .releaseDate(LocalDate.now())
                .duration(60)
                .build();
        final Film twoFilm = Film.builder()
                .id(2L)
                .name("Test name 2")
                .description("Test description 2")
                .releaseDate(LocalDate.now())
                .duration(100)
                .build();

        filmController.getIdToFilm().put(film.getId(), film);
        filmController.getIdToFilm().put(twoFilm.getId(), twoFilm);

        Collection<FilmDto> all = filmController.findAll();

        assertEquals(2, all.size(), "findAll");
    }

    @Test
    void createOk() {
        final CreateFilmDto createFilmDto = new CreateFilmDto(1L,
                "Test name",
                "Test description",
                LocalDate.now(),
                60);

        List<ConstraintViolation<CreateFilmDto>> violations = new ArrayList<>(validator.validate(createFilmDto));

        assertTrue(violations.isEmpty(), "create");
    }

    @Test
    void incorrectFieldsCreate() {
        final CreateFilmDto incorrectFieldsFilmDto = new CreateFilmDto(1L,
                "",
                "Test description".repeat(50), // description > 200 chars
                LocalDate.of(1895, 12, 27),
                -2);

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

        filmController.getIdToFilm().put(twoCreateFilmDto.getId(), MappingDto.mapCreateFilmDtoToFilm(twoCreateFilmDto));

        List<ConstraintViolation<CreateFilmDto>> violations = new ArrayList<>(validator.validate(incorrectFieldsFilmDto));

        List<String> listNameFields = violations.stream()
                .map(violation -> (((ConstraintViolationImpl) violation).getPropertyPath()))
                .map(pathImpl -> ((PathImpl) pathImpl).getLeafNode().getName())
                .toList();

        assertAll("incorrectFieldsCreate",
                () -> assertEquals(4, violations.size()),
                () -> assertTrue(listNameFields.contains("name")),
                () -> assertTrue(listNameFields.contains("description")),
                () -> assertTrue(listNameFields.contains("releaseDate")),
                () -> assertTrue(listNameFields.contains("duration")),
                () -> assertThrows(ValidationException.class,
                        () -> filmController.create(duplicateNameTwoCreateFilmDto))); // duplicate name
    }

    @Test
    void updateOk() {
        final UpdateFilmDto updateFilmDto = new UpdateFilmDto(1L,
                "Test new name",
                "Test new description",
                LocalDate.now(),
                120);

        filmController.getIdToFilm().put(updateFilmDto.getId(), MappingDto.mapUpdateFilmDtoToFilm(updateFilmDto));

        List<ConstraintViolation<UpdateFilmDto>> violations = new ArrayList<>(validator.validate(updateFilmDto));

        assertTrue(violations.isEmpty(), "update");
    }

    @Test
    void incorrectFieldsUpdate() {
        final UpdateFilmDto incorrectFieldsFilmDto = new UpdateFilmDto(-1L,
                "",
                "Test new description".repeat(50), // description > 200 chars
                LocalDate.of(1895, 12, 27),
                -2);

        final UpdateFilmDto filmDtoOk = new UpdateFilmDto(10L,
                "Test name ok",
                "Test new description ok",
                LocalDate.of(2000, 12, 27),
                100);

       filmController.getIdToFilm().put(filmDtoOk.getId(),
                MappingDto.mapUpdateFilmDtoToFilm(filmDtoOk));

        final UpdateFilmDto newFilmDtoOk = new UpdateFilmDto(11L,
                "Test name ok new",
                "Test new description ok new",
                LocalDate.of(2001, 12, 27),
                100);

        final UpdateFilmDto duplicateNewNameFilmDtoOk = new UpdateFilmDto(11L,
                "Test name ok",
                "Test new description ok",
                LocalDate.of(2001, 12, 27),
                100);

        filmController.getIdToFilm().put(newFilmDtoOk.getId(),
                MappingDto.mapUpdateFilmDtoToFilm(newFilmDtoOk));

        List<ConstraintViolation<UpdateFilmDto>> violations = new ArrayList<>(validator.validate(incorrectFieldsFilmDto));

        List<String> listNameFields = violations.stream()
                .map(violation -> (((ConstraintViolationImpl) violation).getPropertyPath()))
                .map(pathImpl -> ((PathImpl) pathImpl).getLeafNode().getName())
                .toList();

        assertAll("incorrectFieldsUpdate",
                () -> assertEquals(5, violations.size()),
                () -> assertTrue(listNameFields.contains("id")),
                () -> assertTrue(listNameFields.contains("name")),
                () -> assertTrue(listNameFields.contains("description")),
                () -> assertTrue(listNameFields.contains("releaseDate")),
                () -> assertTrue(listNameFields.contains("duration")),
                () -> assertThrows(ValidationException.class,
                        () -> filmController.update(incorrectFieldsFilmDto)), // not found by id
                () -> assertThrows(ValidationException.class,
                        () -> filmController.update(duplicateNewNameFilmDtoOk))); // duplicate name
    }
}
