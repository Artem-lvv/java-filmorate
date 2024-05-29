package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityDuplicateException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;


@Deprecated
@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> idToFilm = new HashMap<>();
    @Qualifier("mvcConversionService")
    private final ConversionService cs;

    @Override
    public FilmDto create(CreateFilmDto createFilmDto) {
        Optional<Film> foundMovieByName = idToFilm.values()
                .stream()
                .filter(filmStream -> filmStream.getName().equals(createFilmDto.name()))
                .findFirst();

        if (foundMovieByName.isPresent()) {
            String message = "A film with the same title already exists " + createFilmDto.name();
            log.warn(message);
            throw new EntityDuplicateException("name", createFilmDto.name());
        }

        Film finalFilm = cs.convert(createFilmDto, Film.class);
        finalFilm.setId(getNextId());
        idToFilm.put(finalFilm.getId(), finalFilm);

        log.info("Create {}", finalFilm);

        return cs.convert(finalFilm, FilmDto.class);
    }

    @Override
    public FilmDto update(UpdateFilmDto updateFilmDto) {
        if (findById(updateFilmDto.id()).isEmpty()) {
            String message = "Film not found id " + updateFilmDto.id();
            log.warn(message);

            throw new EntityNotFoundByIdException("Film", updateFilmDto.id().toString());
        }

        Film oldFilm = idToFilm.get(updateFilmDto.id());
        Optional<Film> foundMovieByName = idToFilm.values()
                .stream()
                .filter(film -> film.getName().equals(updateFilmDto.name()))
                .findFirst();

        if (!updateFilmDto.name().equals(oldFilm.getName())
                && foundMovieByName.isPresent()) {
            String message = "A film with the same title already exists " + updateFilmDto.name();
            log.warn(message);
            throw new EntityDuplicateException("name", updateFilmDto.name());
        }

        Film finalFilm = cs.convert(updateFilmDto, Film.class);
        finalFilm.setLikes(oldFilm.getLikes()); // safe likes

        idToFilm.put(finalFilm.getId(), finalFilm);
        log.info("Update: oldObj {} -> newObj {}", oldFilm, updateFilmDto);

        return cs.convert(finalFilm, FilmDto.class);
    }

    public List<FilmDto> findAll() {
        log.info("Find all film, size {}", idToFilm.size());

        return idToFilm.values()
                .stream()
                .map(film -> cs.convert(film, FilmDto.class))
                .sorted(Comparator.comparing(filmDto -> filmDto != null ? filmDto.id() : null))
                .toList();
    }

    @Override
    public Optional<Film> findById(Long id) {
        if (idToFilm.containsKey(id)) {
            return Optional.ofNullable(idToFilm.get(id));
        }

        return Optional.empty();
    }

    @Override
    public List<FilmDto> findPopularFilms(Long count) {
        return idToFilm.values()
                .stream()
                .filter(Objects::nonNull)
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .map(film -> cs.convert(film, FilmDto.class))
                .toList();
    }

    @Override
    public FilmDto findByIdFilmWithGenreAndMpa(Long genreId) {
        return null;
    }

    private long getNextId() {
        long currentMaxId = idToFilm.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
