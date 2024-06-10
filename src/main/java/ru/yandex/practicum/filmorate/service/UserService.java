package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.EntityDuplicateException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.UserRepository;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.feed.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.model.feed.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.feed.Operation.REMOVE;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserStorage {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final UserRepository userRepository;
    private final FeedService feedService;

    @Override
    @Transactional
    public UserDto create(CreateUserDto createUserDto) {
        Optional<User> userByEmail = userRepository.findByEmail(createUserDto.email());

        if (userByEmail.isPresent()) {
            String message = "There is already a user with this email " + createUserDto.email();
            log.warn(message);

            throw new EntityDuplicateException("email", createUserDto.email());
        }

        User finalUser = cs.convert(createUserDto, User.class);

        if (finalUser.getName() == null) {
            finalUser.setName(finalUser.getLogin());
        }

        Long id = userRepository.create(createUserDto);
        finalUser.setId(id);

        log.info("Create {}", finalUser);

        return cs.convert(finalUser, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto update(UpdateUserDto updateUserDto) {
        Optional<User> oldUser = searchUserById(updateUserDto.id());

        if (oldUser.isEmpty()) {
            String message = "User not found id " + updateUserDto.id();
            log.warn(message);

            throw new EntityNotFoundByIdException("User", updateUserDto.id().toString());
        }

        if (!updateUserDto.email().equals(oldUser.get().getEmail())
                && userRepository.findByEmail(updateUserDto.email()).isPresent()) {
            String message = "There is already a user with this email " + updateUserDto.email();
            log.warn(message);
            throw new EntityDuplicateException("email", updateUserDto.email());
        }

        Optional<User> findUserByLogin = userRepository.findByLogin(updateUserDto.login());

        if (!updateUserDto.login().equals(oldUser.get().getLogin())
                && findUserByLogin.isPresent()) {
            String message = "there is already a user with this login " + updateUserDto.login();
            log.warn(message);
            throw new EntityDuplicateException("login", updateUserDto.login());
        }

        User finalUser = cs.convert(updateUserDto, User.class);

        int rowsUpdated = userRepository.update(updateUserDto);

        if (rowsUpdated == 0) {
            String message = "failed to update entity data ";
            log.warn(message + updateUserDto);
            throw new InternalServerException(message);
        }

        log.info("Update: oldObj {} -> newObj {}", oldUser, finalUser);

        return cs.convert(finalUser, UserDto.class);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> cs.convert(user, UserDto.class))
                .toList();
    }

    public Optional<User> searchUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserDto findById(Long id) {
        Optional<User> userById = userRepository.findById(id);
        userById.orElseThrow(() -> new EntityNotFoundByIdException("user", id.toString()));

        return cs.convert(userById.get(), UserDto.class);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        checkUserById(userId);
        checkUserById(friendId);

        userRepository.addFriend(userId, friendId);
        feedService.addFeed(Feed.builder()
                .entityId(friendId)
                .userId(userId)
                .eventType(FRIEND)
                .operation(ADD)
                .build());
    }

    @Override
    public List<UserDto> findAllFriendsUser(Long userId) {
        checkUserById(userId);
        List<User> allFriendsUser = userRepository.findAllFriendsUser(userId);

        return allFriendsUser
                .stream()
                .map(user -> cs.convert(user, UserDto.class))
                .toList();
    }

    @Override
    public void deleteFriendUser(Long userId, Long friendId) {
        checkUserById(userId);
        checkUserById(friendId);

        userRepository.deleteFriendUser(userId, friendId);
        feedService.addFeed(Feed.builder()
                .entityId(friendId)
                .userId(userId)
                .eventType(FRIEND)
                .operation(REMOVE)
                .build());
    }

    @Override
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

    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}
