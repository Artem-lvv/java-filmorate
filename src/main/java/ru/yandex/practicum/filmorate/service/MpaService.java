package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.model.film.dto.MPADto;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.MpaRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaService {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final MpaRepository mpaRepository;

    public List<MPADto> findAll() {
        List<MPA> mpas = mpaRepository.findAll();

        return mpas
                .stream()
                .map(mpa -> cs.convert(mpa, MPADto.class))
                .toList();
    }

    public MPADto findById(Long id) {
        Optional<MPA> mpaById = mpaRepository.findById(id);

        if (mpaById.isEmpty()) {
            throw new EntityNotFoundByIdException("MPA", id.toString());
        }
        return cs.convert(mpaById.get(), MPADto.class);
    }
}
