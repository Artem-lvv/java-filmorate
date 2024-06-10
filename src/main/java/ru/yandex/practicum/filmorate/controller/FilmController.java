package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmStorage.findAll();
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
        filmStorage.addLikeFilm(id, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmStorage.deleteLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> findPopularFilms(@RequestParam Map<String, String> allParams) {
        return filmStorage.findPopularFilms(allParams);
    }

    @GetMapping("/{id}")
    public FilmDto findById(@PathVariable Long id) {
        return filmStorage.findByIdFilmWithGenreAndMpa(id);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> findDirectorFilms(@PathVariable Long directorId,
                                           @RequestParam(defaultValue = "") String sortBy) {
        return filmStorage.findDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<FilmDto> searchFilms(@RequestParam(defaultValue = "") String query,
                                     @RequestParam(name = "by", defaultValue = "") String searchBy) {
        return filmStorage.searchFilms(query, searchBy);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable long filmId) {
        filmStorage.deleteFilm(filmId);
    }

    @GetMapping("/common")
    public Collection<FilmDto> findAllCommonFilms(@RequestParam @Positive final Long userId,
                                                  @RequestParam @Positive final Long friendId) {
        return filmStorage.findAllCommonFilms(userId, friendId);
    }
}
