package ru.yandex.practicum.filmorate.storage.inDataBase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.EntityDuplicateException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserDBStorage implements UserStorage {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final UserRepository userRepository;
    
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
        Optional<User> oldUser = findById(updateUserDto.id());

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

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }


}
