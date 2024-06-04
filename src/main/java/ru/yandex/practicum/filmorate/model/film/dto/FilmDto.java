package ru.yandex.practicum.filmorate.model.film.dto;

import lombok.Builder;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MPA;

import java.time.LocalDate;
import java.util.List;

@Builder
public record FilmDto(
        Long id,
        String name,
        String description,
        LocalDate releaseDate,
        Integer duration,
        List<Genre> genres,
        MPA mpa
) {

}
