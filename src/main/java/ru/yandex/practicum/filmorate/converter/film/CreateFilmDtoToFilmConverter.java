package ru.yandex.practicum.filmorate.converter.film;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;

import java.util.HashSet;

@Component
public class CreateFilmDtoToFilmConverter implements Converter<CreateFilmDto, Film> {
    @Override
    public Film convert(CreateFilmDto src) {
        return Film.builder()
                .id(src.id())
                .name(src.name())
                .description(src.description())
                .releaseDate(src.releaseDate())
                .duration(src.duration())
                .likes(new HashSet<>())
                .build();
    }
}
