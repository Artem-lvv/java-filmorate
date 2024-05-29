package ru.yandex.practicum.filmorate.storage.inDataBase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.model.film.dto.MPADto;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.MPARepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MPADBStorage {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final MPARepository mpaRepository;

    public List<MPADto> findAll() {
        List<MPA> mpas = mpaRepository.findAll();

        return mpas
                .stream()
                .map(mpa -> cs.convert(mpa, MPADto.class))
                .toList();
    }

    public MPADto findById(Long id) {
        Optional<MPA> MPAById = mpaRepository.findById(id);

        if (MPAById.isEmpty()) {
            throw new EntityNotFoundByIdException("MPA", id.toString());
        }
        return cs.convert(MPAById.get(), MPADto.class);
    }
}
