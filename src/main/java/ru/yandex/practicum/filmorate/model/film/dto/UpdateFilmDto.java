package ru.yandex.practicum.filmorate.model.film.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.yandex.practicum.filmorate.annotation.MinDate;

import java.time.LocalDate;
import java.util.Set;

public record UpdateFilmDto(
        @Min(value = 0, message = "Value must be positive")
        Long id,
        @NotBlank(message = "Value must be not empty or null")
        String name,
        @Size(max = 200, message = "Value size must not exceed 200 characters")
        String description,
        @MinDate LocalDate releaseDate,
        @Min(value = 1, message = "Value must be positive")
        Integer duration,
        Set<GenreIdDto> genres,
        MPAIdDto mpa,
        Set<DirectorDto> directors
) {
}
