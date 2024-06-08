package ru.yandex.practicum.filmorate.model.film.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DirectorDto(Long id,
                          @NotBlank(message = "Value must be not empty or null") String name) {
}
