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
import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.GenreIdDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.DirectorRepository;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.FilmRepository;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.GenreRepository;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.MpaRepository;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.feed.EventType.LIKE;
import static ru.yandex.practicum.filmorate.model.feed.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.feed.Operation.REMOVE;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FilmService implements FilmStorage {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final DirectorRepository directorRepository;
    private final FeedService feedService;

    @Override
    @Transactional
    public FilmDto create(CreateFilmDto createFilmDto) {
        Film finalFilm = cs.convert(createFilmDto, Film.class);

        Long filmId = filmRepository.create(createFilmDto);
        finalFilm.setId(filmId);

        log.info("Create {}", finalFilm);

        if (Objects.nonNull(createFilmDto.genres())) {
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

        if (Objects.nonNull(createFilmDto.directors())) {
            List<Director> directorList = createFilmDto.directors()
                    .stream()
                    .map(directorIdDto -> {
                        Optional<Director> director = directorRepository.findById(directorIdDto.id());
                        if (director.isPresent()) {
                            return director.get();
                        }
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "No entity [%s] with id: [%s]".formatted("genre", directorIdDto.id().toString()));
                    })
                    .toList();

            directorList
                    .forEach(director -> {
                        directorRepository.addFilmDirector(filmId, director.getId());
                        log.info("add row FILM_DIRECTOR {} to {}", filmId, director.getId());
                    });

            finalFilm.setDirectors(directorList);
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
    @Transactional
    public FilmDto update(UpdateFilmDto updateFilmDto) {
        Optional<Film> filmById = searchFilmById(updateFilmDto.id());

        if (filmById.isEmpty()) {
            String message = "Film not found id " + updateFilmDto.id();
            log.warn(message);

            throw new EntityNotFoundByIdException("Film", updateFilmDto.id().toString());
        }

        Film oldFilm = filmById.get();

        int rowsUpdated = filmRepository.update(updateFilmDto);

        if (rowsUpdated == 0) {
            String message = "failed to update entity data ";
            log.warn(message + updateFilmDto);
            throw new InternalServerException(message);
        } else {
            log.info("Update: oldObj {} -> newObj {}", oldFilm, updateFilmDto);
        }

        Film finalFilm = cs.convert(updateFilmDto, Film.class);

        if (Objects.nonNull(updateFilmDto.genres())) {
            Set<Long> genresIDByFilmId = genreRepository.findGenresByFilmId(updateFilmDto.id())
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            Set<Long> updateIdGenres = updateFilmDto.genres()
                    .stream()
                    .map(GenreIdDto::id)
                    .collect(Collectors.toSet());

            boolean areEqual = updateIdGenres.equals(genresIDByFilmId);

            if (updateIdGenres.isEmpty() && !genresIDByFilmId.isEmpty() || !areEqual) {
                genresIDByFilmId.forEach(genreIdDto ->
                        genreRepository.deleteFilmGenre(updateFilmDto.id(), genreIdDto));
                updateFilmDto.genres().forEach(genreIdDto ->
                        genreRepository.addFilmGenre(updateFilmDto.id(), genreIdDto.id()));
            }
        }

        List<Genre> genresByFilmId = genreRepository.findGenresByFilmId(updateFilmDto.id());
        finalFilm.setGenres(genresByFilmId);

        if (Objects.nonNull(updateFilmDto.mpa())) {
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
        mpaByFilmId.ifPresent(finalFilm::setMpa);

        Set<Long> directorsID = Optional.ofNullable(updateFilmDto.directors()).orElse(Collections.emptySet())
                .stream()
                .map(DirectorDto::id)
                .collect(Collectors.toSet());
        directorRepository.updateFilmDirectors(updateFilmDto.id(), directorsID);
        finalFilm.setDirectors(directorRepository.findDirectorsByFilmId(finalFilm.getId()));

        return cs.convert(finalFilm, FilmDto.class);
    }

    @Override
    public List<FilmDto> findAll() {
        return filmRepository.findAll()
                .stream()
                .map(film -> {
                    fillFilmFieldsFromOtherTables(film);
                    return cs.convert(film, FilmDto.class);
                })
                .toList();
    }

    public Optional<Film> searchFilmById(Long id) {
        return filmRepository.findById(id);
    }

    @Override
    public List<FilmDto> findPopularFilms(Map<String, String> allParams) {
        Map<String, Long> finalAllParam = allParams.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            try {
                                return Long.parseLong(entry.getValue());
                            } catch (NumberFormatException e) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Incorrect request parameter [%s] value [%s]"
                                                .formatted(entry.getKey(), entry.getValue()));
                            }
                        }
                ));

        if (finalAllParam.containsKey("genreId")) {
            List<Genre> genreById = genreRepository.findById(finalAllParam.get("genreId"));
            if (genreById.isEmpty()) {
                throw new EntityNotFoundByIdException("Genre", finalAllParam.get("genreId").toString());
            }
        }

        // default value "count" is 10L
        if (!finalAllParam.containsKey("count")) {
            long defaultSize = 10L;
            finalAllParam.put("count", defaultSize);
        }

        List<Film> popularFilms = filmRepository.findPopularFilmsBySelection(finalAllParam);

        if (popularFilms.isEmpty()) {
            return Collections.emptyList();
        }

        return popularFilms
                .stream()
                .map(film -> {
                    fillFilmFieldsFromOtherTables(film);
                    return cs.convert(film, FilmDto.class);
                })
                .toList();
    }

    @Override
    public FilmDto findById(Long filmId) {
        Optional<Film> filmById = filmRepository.findById(filmId);

        if (filmById.isEmpty()) {
            throw new EntityNotFoundByIdException("film", filmId.toString());
        }

        fillFilmFieldsFromOtherTables(filmById.get());

        return cs.convert(filmById.get(), FilmDto.class);
    }

    private void fillFilmFieldsFromOtherTables(Film film) {
        Long id = film.getId();
        List<Genre> genresFilm = genreRepository.findGenresByFilmId(id);
        film.setGenres(genresFilm);

        List<Director> directorsFilm = directorRepository.findDirectorsByFilmId(id);
        film.setDirectors(directorsFilm);

        Optional<MPA> mpa = mpaRepository.findMpaByFilmId(id);

        if (mpa.isEmpty()) {
            film.setMpa(MPA.builder().build());
        } else {
            film.setMpa(mpa.get());
        }
    }

    @Override
    public void addLikeFilm(Long id, Long userId) {
        checkEntityById(id, userId);

        filmRepository.addLikeFilm(id, userId);
        feedService.addFeed(Feed.builder()
                .entityId(id)
                .userId(userId)
                .eventType(LIKE)
                .operation(ADD)
                .build());
    }

    @Override
    public void deleteLikeFilm(Long id, Long userId) {
        checkEntityById(id, userId);

        filmRepository.deleteLikeFilm(id, userId);
        feedService.addFeed(Feed.builder()
                .entityId(id)
                .userId(userId)
                .eventType(LIKE)
                .operation(REMOVE)
                .build());
    }

    @Override
    public List<FilmDto> findDirectorFilms(Long directorId, String sortBy) {
        directorRepository.findById(directorId)
                .orElseThrow(() -> new EntityNotFoundByIdException("Director", directorId.toString()));

        List<Film> directorFilms = filmRepository.findDirectorFilms(directorId, sortBy);
        for (Film film : directorFilms) {
            fillFilmFieldsFromOtherTables(film);
        }

        return directorFilms
                .stream()
                .map(film -> cs.convert(film, FilmDto.class))
                .toList();
    }

    @Override
    public List<FilmDto> searchFilms(String query, String searchBy) {
        List<Film> films = filmRepository.searchFilms(query, searchBy);
        for (Film film : films) {
            fillFilmFieldsFromOtherTables(film);
        }

        return films
                .stream()
                .map(film -> cs.convert(film, FilmDto.class))
                .toList();
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

    @Override
    public Set<FilmDto> recommendFilms(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundByIdException("User", userId.toString()));

        List<Long> similarUserIds = filmRepository.findSimilarUsersByLikes(userId);

        Set<Film> recommendedFilms = new HashSet<>();
        for (Long similarUserId : similarUserIds) {
            int maxSize = 10;

            List<Film> films = filmRepository.findRecommendedFilms(userId, similarUserId);
            recommendedFilms.addAll(films);

            if (recommendedFilms.size() >= maxSize) {
                break;
            }
        }

        return recommendedFilms
                .stream()
                .map(film -> {
                    fillFilmFieldsFromOtherTables(film);
                    return cs.convert(film, FilmDto.class);
                })
                .collect(Collectors.toSet());
    }

    public void deleteFilm(Long filmId) {
        filmRepository.deleteFilm(filmId);
    }

    @Override
    public Collection<FilmDto> findAllCommonFilms(final Long userId, final Long friendId) {
        return filmRepository.findAllCommonFilms(userId, friendId)
                .stream()
                .map(film -> {
                    fillFilmFieldsFromOtherTables(film);
                    return cs.convert(film, FilmDto.class);
                }).toList();
    }
}
