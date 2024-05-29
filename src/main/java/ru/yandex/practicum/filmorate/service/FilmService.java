package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.FilmRepository;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService implements FilmStorage {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    public void addLikeFilm(Long id, Long userId) {
        checkEntityById(id, userId);

        filmRepository.addLikeFilm(id, userId);
    }

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
