package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    UserDto create(CreateUserDto createUserDto);

    UserDto update(UpdateUserDto updateUserDto);

    List<UserDto> findAll();

    UserDto findById(Long id);

    void addFriend(Long userId, Long friendId);

    List<UserDto> findAllFriendsUser(Long userId);

    void deleteFriendUser(Long userId, Long friendId);

    List<UserDto> getCommonFriendsUser(Long userId, Long otherId);

    void deleteUser(Long userId);
}
