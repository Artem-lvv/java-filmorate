package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public void addLikeFilm(Long id, Long userId) {
        Map<Long, Film> idToFilm = checkAndGetFilmsById(List.of(id));
        userService.checkAndGetUsersById(List.of(userId));

        idToFilm.get(id).getLikes().add(userId);
    }

    public void deleteLikeFilm(Long id, Long userId) {
        Map<Long, Film> idToFilm = checkAndGetFilmsById(List.of(id));
        userService.checkAndGetUsersById(List.of(userId));

        idToFilm.get(id).getLikes().remove(userId);
    }

    public Map<Long, Film> checkAndGetFilmsById(List<Long> listId) {
        return listId.stream()
                .map(filmId -> {
                    Optional<Film> film = filmStorage.findById(filmId);
                    if (film.isEmpty()) {
                        throw new EntityNotFoundByIdException("film", filmId.toString());
                    }
                    return film.get();
                })
                .collect(Collectors.toMap(Film::getId, v -> v));
    }
}
