package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    @Qualifier("filmDBStorage")
    private final FilmStorage filmStorage;

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody CreateFilmDto createFilmDto) {
        return filmStorage.create(createFilmDto);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmDto updateFilmDto) {
        return filmStorage.update(updateFilmDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLikeFilm(id, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLikeFilm(id, userId);
    }

   @GetMapping("/popular")
    public List<FilmDto> findPopularFilms(@RequestParam(defaultValue = "10") Long count) {
        return filmStorage.findPopularFilms(count);
    }

    @GetMapping("/{id}")
    public FilmDto findById(@PathVariable Long id) {
        return filmStorage.findByIdFilmWithGenreAndMpa(id);
    }

}
