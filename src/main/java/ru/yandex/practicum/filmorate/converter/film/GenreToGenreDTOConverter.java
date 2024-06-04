package ru.yandex.practicum.filmorate.converter.film;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.dto.GenreDto;

@Component
public class GenreToGenreDTOConverter implements Converter<Genre, GenreDto> {
    @Override
    public GenreDto convert(Genre src) {
        return GenreDto.builder()
                .id(src.getId())
                .name(src.getName())
                .description(src.getDescription())
                .build();
    }
}
