package ru.yandex.practicum.filmorate.converter.film;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.dto.DirectorDto;

@Component
public class DirectorToDirectorDtoConverter implements Converter<Director, DirectorDto> {
    @Override
    public DirectorDto convert(Director source) {
        return DirectorDto.builder()
                .id(source.getId())
                .name(source.getName())
                .build();
    }
}
