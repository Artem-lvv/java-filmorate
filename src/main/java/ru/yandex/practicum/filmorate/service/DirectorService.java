package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.dto.DirectorDto;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.DirectorRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final DirectorRepository directorRepository;

    public List<DirectorDto> findAll() {
        List<Director> directors = directorRepository.findAll();

        return directors.stream().map(director -> cs.convert(director, DirectorDto.class)).toList();
    }

    public void addFilmDirector(Long filmId, Long directorId) {
        directorRepository.addFilmDirector(filmId, directorId);
    }

    public DirectorDto findById(Long id) {
        Optional<Director> directorById = directorRepository.findById(id);

        if (directorById.isEmpty()) {
            throw new EntityNotFoundByIdException("director", id.toString());
        }
        return cs.convert(directorById.get(), DirectorDto.class);
    }

    @Transactional
    public DirectorDto create(DirectorDto createDirectorDto) {
        Director finalDirector = cs.convert(createDirectorDto, Director.class);

        Long directorId = directorRepository.create(createDirectorDto);
        finalDirector.setId(directorId);

        log.info("Create {}", finalDirector);

        return cs.convert(finalDirector, DirectorDto.class);
    }

    @Transactional
    public DirectorDto update(DirectorDto updateDirectorDto) {
        Optional<Director> directorById = directorRepository.findById(updateDirectorDto.id());

        if (directorById.isEmpty()) {
            String message = "Director not found id " + updateDirectorDto.id();
            log.warn(message);

            throw new EntityNotFoundByIdException("Director", updateDirectorDto.id().toString());
        }

        Director oldDirector = directorById.get();

        int rowsUpdated = directorRepository.update(updateDirectorDto);
        if (rowsUpdated == 0) {
            String message = "failed to update entity data ";
            log.warn(message + updateDirectorDto);
            throw new InternalServerException(message);
        } else {
            log.info("Update: oldObj {} -> newObj {}", oldDirector, updateDirectorDto);
        }

        Director finalDirector = cs.convert(updateDirectorDto, Director.class);

        return cs.convert(finalDirector, DirectorDto.class);
    }

    public void deleteDirector(Long id) {
        Optional<Director> director = directorRepository.findById(id);
        if (director.isEmpty()) {
            throw new EntityNotFoundByIdException("director", id.toString());
        }
        directorRepository.deleteDirector(id);
    }
}
