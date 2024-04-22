package ru.yandex.practicum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
}
