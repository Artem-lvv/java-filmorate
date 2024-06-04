package ru.yandex.practicum.filmorate.converter.film;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.model.film.dto.MPADto;

@Component
public class MpaToMpaDTOConverter implements Converter<MPA, MPADto> {
    @Override
    public MPADto convert(MPA src) {
        return MPADto.builder()
                .id(src.getId())
                .name(src.getName())
                .description(src.getDescription())
                .build();
    }
}
