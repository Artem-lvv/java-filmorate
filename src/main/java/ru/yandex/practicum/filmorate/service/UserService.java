package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final UserStorage userStorage;

    public void addFriend(Long id, Long friendId) {
        Map<Long, User> idToUser = checkAndGetUsersById(List.of(id, friendId));

        idToUser.get(id).getFriends().add(friendId);
        idToUser.get(friendId).getFriends().add(id);
    }

    public List<UserDto> findAllFriendsUser(Long id) {
        Optional<User> user = userStorage.findById(id);

        if (user.isEmpty()) {
            throw new EntityNotFoundByIdException("User", id.toString());
        }

        return user.get().getFriends()
                .stream()
                .map(userStorage::findById)
                .filter(Optional::isPresent)
                .map(streamUser -> cs.convert(streamUser.get(), UserDto.class))
                .sorted(Comparator.comparing(userDto -> userDto != null ? userDto.id() : null))
                .toList();
    }

    public void deleteFriendUser(Long id, Long friendId) {
        Map<Long, User> idToUser = checkAndGetUsersById(List.of(id, friendId));

        idToUser.get(id).getFriends().remove(friendId);
        idToUser.get(friendId).getFriends().remove(id);
    }

    public List<UserDto> getCommonFriendsUser(Long id, Long otherId) {
        Map<Long, User> idToUser = checkAndGetUsersById(List.of(id, otherId));
        User user = idToUser.get(id);
        User otherUser = idToUser.get(otherId);

        return user.getFriends()
                .stream()
                .filter(idFriend -> otherUser.getFriends().contains(idFriend))
                .map(idCommonFriend -> {
                    Optional<User> commonFriend = userStorage.findById(idCommonFriend);
                    if (commonFriend.isEmpty()) {
                        throw new EntityNotFoundByIdException("User", idCommonFriend.toString());
                    }
                    return commonFriend.get();
                })
                .map(streamUser -> cs.convert(streamUser, UserDto.class))
                .sorted(Comparator.comparing(userDto -> userDto != null ? userDto.id() : null))
                .toList();
    }

    public Map<Long, User> checkAndGetUsersById(List<Long> listId) {
        return listId.stream()
                .map(userId -> {
                    Optional<User> user = userStorage.findById(userId);
                    if (user.isEmpty()) {
                        throw new EntityNotFoundByIdException("User", userId.toString());
                    }
                    return user.get();
                })
                .collect(Collectors.toMap(User::getId, v -> v));
    }
}
