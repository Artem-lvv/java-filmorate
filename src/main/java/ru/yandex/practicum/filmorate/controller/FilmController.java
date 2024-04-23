package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityDuplicateException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;

import java.util.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> idToFilm = new HashMap<>();

    @Qualifier("mvcConversionService")
    private final ConversionService cs;

    @GetMapping
    public Collection<FilmDto> findAll() {
        log.info("Find all film, size {}", idToFilm.size());
        return idToFilm.values()
                .stream()
                .map(film -> cs.convert(film, FilmDto.class))
                .sorted(Comparator.comparing(filmDto -> filmDto != null ? filmDto.getId() : null))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody CreateFilmDto createFilmDto) {
        Optional<Film> foundMovieByName = idToFilm.values()
                .stream()
                .filter(filmStream -> filmStream.getName().equals(createFilmDto.name()))
                .findFirst();

        if (foundMovieByName.isPresent()) {
            String message = "A film with the same title already exists " + createFilmDto.name();
            log.warn(message);
            throw new EntityDuplicateException("name", createFilmDto.name());
        }

        Film finalFilm = cs.convert(createFilmDto, Film.class);

        finalFilm.setId(getNextId());
        idToFilm.put(finalFilm.getId(), finalFilm);
        log.info("Create {}", finalFilm);

        return finalFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody UpdateFilmDto updateFilmDto) {
        if (!idToFilm.containsKey(updateFilmDto.id())) {
            String message = "Film not found id " + updateFilmDto.id();
            log.warn(message);
            throw new EntityNotFoundException(updateFilmDto.id().toString());
        }

        Film oldFilm = idToFilm.get(updateFilmDto.id());
        Optional<Film> foundMovieByName = idToFilm.values()
                .stream()
                .filter(film -> film.getName().equals(updateFilmDto.name()))
                .findFirst();

        if (!updateFilmDto.name().equals(oldFilm.getName())
                && foundMovieByName.isPresent()) {
            String message = "A film with the same title already exists " + updateFilmDto.name();
            log.warn(message);
            throw new EntityDuplicateException("name", updateFilmDto.name());
        }

        Film finalFilm = cs.convert(updateFilmDto, Film.class);

        idToFilm.put(finalFilm.getId(), finalFilm);
        log.info("Update: oldObj {} -> newObj {}", oldFilm, updateFilmDto);

        return finalFilm;
    }

    private long getNextId() {
        long currentMaxId = idToFilm.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
