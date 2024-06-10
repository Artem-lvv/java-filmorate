package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;

import java.util.*;

public interface FilmStorage {
    FilmDto create(CreateFilmDto createFilmDto);

    FilmDto update(UpdateFilmDto updateFilmDto);

    List<FilmDto> findAll();

    Optional<Film> findById(Long id);

    List<FilmDto> findPopularFilms(Map<String, String> allParams);

    FilmDto findByIdFilmWithGenreAndMpa(Long genreId);

    void addLikeFilm(Long id, Long userId);

    void deleteLikeFilm(Long id, Long userId);

    Set<FilmDto> recommendFilms(Long id);

    List<FilmDto> findDirectorFilms(Long directorId, String sortBy);

    List<FilmDto> searchFilms(String query, String searchBy);

    Collection<FilmDto> findAllCommonFilms(final Long userId, final Long friendId);
}
