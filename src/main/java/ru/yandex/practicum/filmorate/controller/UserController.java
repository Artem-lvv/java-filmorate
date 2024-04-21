package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;
import ru.yandex.practicum.filmorate.service.MappingDto;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Getter
    private final Map<String, User> emailToUser = new HashMap<>();

    @GetMapping
    public Collection<UserDto> findAll() {
        log.info("Find all users, size {}", emailToUser.size());
        return emailToUser.values()
                .stream()
                .map(MappingDto::mapUserToUserDto)
                .sorted(Comparator.comparing(UserDto::getId))
                .toList();
    }

    @PostMapping
    public User create(@Valid @RequestBody CreateUserDto createUserDto) {
        if (emailToUser.containsKey(createUserDto.getEmail())) {
            String message = "There is already a user with this email " + createUserDto.getEmail();
            log.warn(message);
            throw new ValidationException(message);
        }

        User finalUser = MappingDto.mapCreateUserDtoToUser(createUserDto);

        if (finalUser.getName() == null) {
            finalUser.setName(finalUser.getLogin());
        }

        finalUser.setId(getNextId());

        emailToUser.put(finalUser.getEmail(), finalUser);
        log.info("Create {}", finalUser);
        return finalUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody UpdateUserDto updateUserDto) {
        Optional<User> oldUser = emailToUser.values()
                .stream()
                .filter(user -> user.getId().equals(updateUserDto.getId()))
                .findFirst();

        if (oldUser.isEmpty()) {
            String message = "User not found id " + updateUserDto.getId();
            log.warn(message);
            throw new ValidationException(message);
        }

        if (!updateUserDto.getEmail().equals(oldUser.get().getEmail())
                && emailToUser.containsKey(updateUserDto.getEmail())) {
            String message = "There is already a user with this email " + oldUser.get().getEmail();
            log.warn(message);
            throw new ValidationException(message);
        }

        User finalUser = MappingDto.mapUpdateUserDtoToUser(updateUserDto);

        emailToUser.put(finalUser.getEmail(), finalUser);
        log.info("Update: oldObj {} -> newObj {}", oldUser, finalUser);

        return finalUser;
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
