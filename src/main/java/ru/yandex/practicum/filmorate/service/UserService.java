package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.UserRepository;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    @Qualifier("userDBStorage")
    private final UserStorage userStorage;

    private final UserRepository userRepository;

    public void addFriend(Long userId, Long friendId) {
        checkUserById(userId);
        checkUserById(friendId);

        userRepository.addFriend(userId, friendId);
    }

    public List<UserDto> findAllFriendsUser(Long userId) {
        checkUserById(userId);
        List<User> allFriendsUser = userRepository.findAllFriendsUser(userId);

        return allFriendsUser
                .stream()
                .map(user -> cs.convert(user, UserDto.class))
                .toList();
    }

    public void deleteFriendUser(Long userId, Long friendId) {
        checkUserById(userId);
        checkUserById(friendId);

        userRepository.deleteFriendUser(userId, friendId);
    }

    public List<UserDto> getCommonFriendsUser(Long userId, Long otherId) {
        List<User> commonFriendsUser = userRepository.getCommonFriendsUser(userId, otherId);

        return commonFriendsUser
                .stream()
                .map(user -> cs.convert(user, UserDto.class))
                .toList();
    }

    public void checkUserById(Long userId) {
        Optional<User> userById = userRepository.findById(userId);
        if (userById.isEmpty()) {
            throw new EntityNotFoundByIdException("user", userId.toString());
        }
    }
}
