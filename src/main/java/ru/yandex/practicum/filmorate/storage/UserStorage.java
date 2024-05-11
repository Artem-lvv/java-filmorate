package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    UserDto create(CreateUserDto createUserDto);

    UserDto update(UpdateUserDto updateUserDto);

    List<UserDto> findAll();

    Optional<User> findById(Long id);
}
