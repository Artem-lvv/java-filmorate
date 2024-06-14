package ru.yandex.practicum.filmorate.converter.film;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;

import java.util.ArrayList;

@Component
public class FilmToFilmDtoConverter implements Converter<Film, FilmDto> {
    @Override
    public FilmDto convert(Film src) {
        return FilmDto.builder()
                .id(src.getId())
                .name(src.getName())
                .description(src.getDescription())
                .releaseDate(src.getReleaseDate())
                .duration(src.getDuration())
                .genres(src.getGenres() == null ? null : new ArrayList<>(src.getGenres()))
                .mpa(src.getMpa())
                .directors(src.getDirectors() == null ? null : new ArrayList<>(src.getDirectors()))
                .build();
    }
}
