package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    FilmDto create(CreateFilmDto createFilmDto);

    FilmDto update(UpdateFilmDto updateFilmDto);

    List<FilmDto> findAll();

    Optional<Film> findById(Long id);

    List<FilmDto> findPopularFilms(Long count);
}
