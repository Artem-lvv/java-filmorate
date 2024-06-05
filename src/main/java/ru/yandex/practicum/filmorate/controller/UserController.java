package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userStorage.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody CreateUserDto createUserDto) {
        return userStorage.create(createUserDto);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserDto updateUserDto) {
        return userStorage.update(updateUserDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userStorage.addFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> findAllFriendsUser(@PathVariable Long id) {
        return userStorage.findAllFriendsUser(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendUser(@PathVariable Long id, @PathVariable Long friendId) {
        userStorage.deleteFriendUser(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriendsUser(@PathVariable Long id, @PathVariable Long otherId) {
        return userStorage.getCommonFriendsUser(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<FilmDto> recommendFilms(@PathVariable Long id) {
        return filmStorage.recommendFilms(id);
    }

}
