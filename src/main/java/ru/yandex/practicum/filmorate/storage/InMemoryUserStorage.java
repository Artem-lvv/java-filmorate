package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityDuplicateException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    @Getter
    private final Map<String, User> emailToUser = new HashMap<>();
    @Qualifier("mvcConversionService")
    private final ConversionService cs;

    @Override
    public UserDto create(CreateUserDto createUserDto) {
        if (emailToUser.containsKey(createUserDto.email())) {
            String message = "There is already a user with this email " + createUserDto.email();
            log.warn(message);

            throw new EntityDuplicateException("email", createUserDto.email());
        }

        User finalUser = cs.convert(createUserDto, User.class);

        if (finalUser.getName() == null) {
            finalUser.setName(finalUser.getLogin());
        }

        finalUser.setId(getNextId());
        emailToUser.put(finalUser.getEmail(), finalUser);

        log.info("Create {}", finalUser);

        return cs.convert(finalUser, UserDto.class);
    }

    @Override
    public UserDto update(UpdateUserDto updateUserDto) {
        Optional<User> oldUser = findById(updateUserDto.id());

        if (oldUser.isEmpty()) {
            String message = "User not found id " + updateUserDto.id();
            log.warn(message);

            throw new EntityNotFoundByIdException("User", updateUserDto.id().toString());
        }

        if (!updateUserDto.email().equals(oldUser.get().getEmail())
                && emailToUser.containsKey(updateUserDto.email())) {
            String message = "There is already a user with this email " + updateUserDto.email();
            log.warn(message);
            throw new EntityDuplicateException("email", updateUserDto.email());
        }

        Optional<User> findUserByLogin = emailToUser.values()
                .stream()
                .filter(user -> user.getLogin().equals(updateUserDto.login()))
                .findFirst();

        if (!updateUserDto.login().equals(oldUser.get().getLogin())
                && findUserByLogin.isPresent()) {
            String message = "there is already a user with this login " + updateUserDto.login();
            log.warn(message);
            throw new EntityDuplicateException("login", updateUserDto.login());
        }

        User finalUser = cs.convert(updateUserDto, User.class);
        finalUser.setFriends(oldUser.get().getFriends()); // save friends

        emailToUser.put(finalUser.getEmail(), finalUser);

        log.info("Update: oldObj {} -> newObj {}", oldUser, finalUser);

        return cs.convert(finalUser, UserDto.class);
    }

    @Override
    public List<UserDto> findAll() {
        log.info("Find all users, size {}", emailToUser.size());

        return emailToUser.values()
                .stream()
                .map(user -> cs.convert(user, UserDto.class))
                .sorted(Comparator.comparing(userDto -> userDto != null ? userDto.id() : null))
                .toList();
    }

    @Override
    public Optional<User> findById(Long id) {
        return emailToUser.values()
                .stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    private long getNextId() {
        long currentMaxId = emailToUser.values()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

}
