package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    void deleteFilm(Long filmId);

    Collection<FilmDto> findAllCommonFilms(final Long userId, final Long friendId);
}
