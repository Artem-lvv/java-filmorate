package ru.yandex.practicum.filmorate.storage.inDataBase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.dto.GenreDto;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.GenreRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDBStorage {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final GenreRepository genreRepository;

    public List<GenreDto> findAll() {
        List<Genre> genres = genreRepository.findAll();

        return genres
                .stream()
                .map(genre -> cs.convert(genre, GenreDto.class))
                .toList();
    }

    public void addFilmGenre(Long filmId, Long genreId) {
        genreRepository.addFilmGenre(filmId, genreId);
    }

    public GenreDto findById(Long id) {
        List<Genre> genreById = genreRepository.findById(id);

        if (genreById.isEmpty()) {
            throw new EntityNotFoundByIdException("genre", id.toString());
        }
        return cs.convert(genreById.get(0), GenreDto.class);
    }
}
