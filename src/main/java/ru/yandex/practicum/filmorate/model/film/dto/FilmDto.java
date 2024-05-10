package ru.yandex.practicum.filmorate.model.film.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record FilmDto(
        Long id,
        String name,
        String description,
        LocalDate releaseDate,
        Integer duration,
        Integer likes
) {

}
