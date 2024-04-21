package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.service.MappingDto;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    @Getter
    private final Map<Long, Film> idToFilm = new HashMap<>();

    @GetMapping
    public Collection<FilmDto> findAll() {
        log.info("Find all film, size {}", idToFilm.size());
        return idToFilm.values()
                .stream()
                .map(MappingDto::mapFilmToFilmDto)
                .sorted(Comparator.comparing(FilmDto::getId))
                .toList();
    }

    @PostMapping
    public Film create(@Valid @RequestBody CreateFilmDto createFilmDto) {
        Optional<Film> foundMovieByName = idToFilm.values()
                .stream()
                .filter(filmStream -> filmStream.getName().equals(createFilmDto.getName()))
                .findFirst();

        if (foundMovieByName.isPresent()) {
            String message = "A film with the same title already exists " + createFilmDto.getName();
            log.warn(message);
            throw new ValidationException(message);
        }

        Film finalFilm = MappingDto.mapCreateFilmDtoToFilm(createFilmDto);

        finalFilm.setId(getNextId());
        idToFilm.put(finalFilm.getId(), finalFilm);
        log.info("Create {}", finalFilm);

        return finalFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody UpdateFilmDto updateFilmDto) {
        if (!idToFilm.containsKey(updateFilmDto.getId())) {
            String message = "Film not found id " + updateFilmDto.getId();
            log.warn(message);
            throw new ValidationException(message);
        }

        Film oldFilm = idToFilm.get(updateFilmDto.getId());
        Optional<Film> foundMovieByName = idToFilm.values()
                .stream()
                .filter(film -> film.getName().equals(updateFilmDto.getName()))
                .findFirst();

        if (!updateFilmDto.getName().equals(oldFilm.getName())
                && foundMovieByName.isPresent()) {
            String message = "A film with the same title already exists " + updateFilmDto.getName();
            log.warn(message);
            throw new ValidationException(message);
        }

        Film finalFilm = MappingDto.mapUpdateFilmDtoToFilm(updateFilmDto);

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
