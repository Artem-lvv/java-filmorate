package ru.yandex.practicum.filmorate.model.film.dto;

import lombok.Builder;

@Builder
public record MPADto(Long id,
                     String name,
                     String description
){
}
