package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.FilmRepository;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.GenreRepository;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.MPARepository;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService implements FilmStorage {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final GenreRepository genreRepository;
    private final MPARepository mpaRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    @Override
    public FilmDto create(CreateFilmDto createFilmDto) {
        Film finalFilm = cs.convert(createFilmDto, Film.class);

        Long filmId = filmRepository.create(createFilmDto);
        finalFilm.setId(filmId);

        log.info("Create {}", finalFilm);

        if (createFilmDto.genres() != null) {
            List<Genre> genreList = createFilmDto.genres()
                    .stream()
                    .map(genreIdDto -> {
                        List<Genre> genres = genreRepository.findById(genreIdDto.id());
                        if (!genres.isEmpty()) {
                            return genres.get(0);
                        }
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "No entity [%s] with id: [%s]".formatted("genre", genreIdDto.id().toString()));
                    })
                    .toList();

            genreList
                    .forEach(genre -> {
                        genreRepository.addFilmGenre(filmId, genre.getId());
                        log.info("add row FILM_GENRE {} to {}", filmId, genre.getId());
                    });

            finalFilm.setGenres(genreList);
        }

        Optional<MPA> byIdMPA = mpaRepository.findById(createFilmDto.mpa().id());

        if (byIdMPA.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No entity [%s] with id: [%s]".formatted("genre", createFilmDto.mpa().id().toString()));
        }

        mpaRepository.addFilmMpa(filmId, createFilmDto.mpa().id());
        log.info("add row FILM_MPA {} to {}", filmId, createFilmDto.mpa().id());
        finalFilm.setMpa(byIdMPA.get());

        return cs.convert(finalFilm, FilmDto.class);
    }

    @Override
    public FilmDto update(UpdateFilmDto updateFilmDto) {
        Optional<Film> filmById = findById(updateFilmDto.id());

        if (filmById.isEmpty()) {
            String message = "Film not found id " + updateFilmDto.id();
            log.warn(message);

            throw new EntityNotFoundByIdException("Film", updateFilmDto.id().toString());
        }

        Film oldFilm = filmById.get();
        Optional<Film> foundMovieByName = filmRepository.findByName(updateFilmDto.name());

//        if (!updateFilmDto.name().equals(oldFilm.getName())
//                && foundMovieByName.isPresent()) {
//            String message = "A film with the same title already exists " + updateFilmDto.name();
//            log.warn(message);
//            throw new EntityDuplicateException("name", updateFilmDto.name());
//        }

        int rowsUpdated = filmRepository.update(updateFilmDto);

        if (rowsUpdated == 0) {
            String message = "failed to update entity data ";
            log.warn(message + updateFilmDto);
            throw new InternalServerException(message);
        } else {
            log.info("Update: oldObj {} -> newObj {}", oldFilm, updateFilmDto);
        }

        Film finalFilm = cs.convert(updateFilmDto, Film.class);

        if (updateFilmDto.genres() != null) {
            Set<Long> genresIDByFilmId = genreRepository.findGenresByFilmId(updateFilmDto.id())
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            boolean areEqual = updateFilmDto.genres().containsAll(genresIDByFilmId);

            if (!areEqual) {
                updateFilmDto.genres().forEach(genreIdDto -> {
                    genreRepository.deleteFilmGenre(updateFilmDto.id(), genreIdDto.id());
                    genreRepository.addFilmGenre(updateFilmDto.id(), genreIdDto.id());
                });
            }
        }

        List<Genre> genresByFilmId = genreRepository.findGenresByFilmId(updateFilmDto.id());
        finalFilm.setGenres(genresByFilmId);

        if (updateFilmDto.mpa() != null) {
            Optional<MPA> byIdMpa = mpaRepository.findById(updateFilmDto.mpa().id());

            if (byIdMpa.isEmpty()) {
                throw new EntityNotFoundByIdException("MPA", updateFilmDto.mpa().id().toString());
            }

            Optional<MPA> mpaByFilmId = mpaRepository.findMpaByFilmId(updateFilmDto.id());
            if (!byIdMpa.get().equals(mpaByFilmId.get())) {
                mpaRepository.deleteFilmMpa(updateFilmDto.id());
                mpaRepository.addFilmMpa(updateFilmDto.id(), updateFilmDto.mpa().id());
            }
        }

        Optional<MPA> mpaByFilmId = mpaRepository.findMpaByFilmId(updateFilmDto.id());
        mpaByFilmId.ifPresent(mpa -> finalFilm.setMpa(mpa));

        return cs.convert(finalFilm, FilmDto.class);
    }

    @Override
    public List<FilmDto> findAll() {
        return filmRepository.findAll()
                .stream()
                .map(film -> cs.convert(film, FilmDto.class))
                .toList();
    }

    @Override
    public Optional<Film> findById(Long id) {
        return filmRepository.findById(id);
    }

    @Override
    public List<FilmDto> findPopularFilms(Long count) {
        List<Film> popularFilms = filmRepository.findPopularFilms(count);

        return popularFilms
                .stream()
                .map(film -> cs.convert(film, FilmDto.class))
                .toList();
    }

    @Override
    public FilmDto findByIdFilmWithGenreAndMpa(Long filmId) {
        Optional<Film> filmById = filmRepository.findById(filmId);

        if (filmById.isEmpty()) {
            throw new EntityNotFoundByIdException("film", filmId.toString());
        }

        List<Genre> genresFilm = genreRepository.findGenresByFilmId(filmId);
        filmById.get().setGenres(genresFilm);

        Optional<MPA> mpa = mpaRepository.findMpaByFilmId(filmId);

        if (mpa.isEmpty()) {
            filmById.get().setMpa(MPA.builder().build());
        } else {
            filmById.get().setMpa(mpa.get());
        }

        return cs.convert(filmById.get(), FilmDto.class);
    }

    @Override
    public void addLikeFilm(Long id, Long userId) {
        checkEntityById(id, userId);

        filmRepository.addLikeFilm(id, userId);
    }

    @Override
    public void deleteLikeFilm(Long id, Long userId) {
        checkEntityById(id, userId);

        filmRepository.deleteLikeFilm(id, userId);
    }

    public void checkEntityById(Long id, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundByIdException("user", userId.toString());
        }

        Optional<Film> film = filmRepository.findById(id);
        if (film.isEmpty()) {
            throw new EntityNotFoundByIdException("film", id.toString());
        }
    }
}
