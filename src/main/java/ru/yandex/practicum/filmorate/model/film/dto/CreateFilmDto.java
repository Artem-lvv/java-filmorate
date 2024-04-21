package ru.yandex.practicum.filmorate.model.film.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.yandex.practicum.filmorate.annotation.MinDate;

import java.io.Serializable;
import java.time.LocalDate;

@Value
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
public class CreateFilmDto implements Serializable {
    private Long id;
    @NotBlank(message = "Value must be not empty or null")
    private String name;
    @Size(max = 200, message = "Value size must not exceed 200 characters")
    private String description;
    @MinDate
    private LocalDate releaseDate;
    @Min(value = 1, message = "Value must be positive")
    private Integer duration;
}
