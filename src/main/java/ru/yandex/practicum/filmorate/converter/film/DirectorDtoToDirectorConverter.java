package ru.yandex.practicum.filmorate.converter.film;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.dto.DirectorDto;

@Component
public class DirectorDtoToDirectorConverter  implements Converter<DirectorDto, Director> {
    @Override
    public Director convert(DirectorDto source) {
        return Director.builder()
                .id(source.id())
                .name(source.name())
                .build();
    }
}
