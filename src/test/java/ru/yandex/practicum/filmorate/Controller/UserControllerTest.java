package ru.yandex.practicum.filmorate.Controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;
import ru.yandex.practicum.filmorate.service.MappingDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
    }

    @Test
    void findAll() {
        final User user = User.builder()
                .id(1L)
                .email("Test1@tt.ru")
                .login("TestLogin")
                .name("Test name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        final User twoUser = User.builder()
                .id(2L)
                .email("Test2@tt.ru")
                .login("TestLogin2")
                .name("Test name 2")
                .birthday(LocalDate.of(2010, 1, 1))
                .build();

        userController.getEmailToUser().put(user.getEmail(), user);
        userController.getEmailToUser().put(twoUser.getEmail(), twoUser);

        Collection<UserDto> all = userController.findAll();

        assertEquals(2, all.size(), "findAll");
    }

    @Test
    void createOk() {
        final CreateUserDto createUserDto = new CreateUserDto(1L,
                "Test1@tt.ru",
                "TestName",
                "Test description",
                LocalDate.of(2010, 1, 1));

        List<ConstraintViolation<CreateUserDto>> violations = new ArrayList<>(validator.validate(createUserDto));

        assertTrue(violations.isEmpty(), "create");
    }

    @Test
    void incorrectFieldsCreate() {
        final CreateUserDto incorrectFieldsUserDto = new CreateUserDto(1L,
                "@ttt.ru", //
                "Test login", // contains a space
                "Test name",
                LocalDate.now().plusYears(10)); // future date

        final CreateUserDto twoUser = new CreateUserDto(2L,
                "two@tt.ru",
                "TestTwo",
                "Test name two",
                LocalDate.now().minusYears(10));

        final CreateUserDto duplicateFieldEmailTwoUser = new CreateUserDto(3L,
                "two@tt.ru",
                "TestThree",
                "Test name three",
                LocalDate.now().minusYears(15));

        userController.getEmailToUser().put(twoUser.getEmail(), MappingDto.mapCreateUserDtoToUser(twoUser));

        List<ConstraintViolation<CreateUserDto>> violations = new ArrayList<>(validator.validate(incorrectFieldsUserDto));

        List<String> listNameFields = violations.stream()
                .map(violation -> (((ConstraintViolationImpl) violation).getPropertyPath()))
                .map(pathImpl -> ((PathImpl) pathImpl).getLeafNode().getName())
                .toList();

        assertAll("incorrectFieldsCreate",
                () -> assertEquals(3, violations.size()),
                () -> assertTrue(listNameFields.contains("email")),
                () -> assertTrue(listNameFields.contains("login")),
                () -> assertTrue(listNameFields.contains("birthday")),
                () -> assertThrows(ValidationException.class,
                        () -> userController.create(duplicateFieldEmailTwoUser))); // duplicate email
    }

    @Test
    void updateOk() {
        final UpdateUserDto updateUserDto = new UpdateUserDto(1L,
                "Test1@tt.ru",
                "TestName",
                "Test description",
                LocalDate.of(2010, 1, 1));

        userController.getEmailToUser().put(updateUserDto.getEmail(), MappingDto.mapUpdateUserDtoToUser(updateUserDto));

        List<ConstraintViolation<UpdateUserDto>> violations = new ArrayList<>(validator.validate(updateUserDto));

        assertTrue(violations.isEmpty(), "update");
    }

    @Test
    void incorrectFieldsUpdate() {
        final UpdateUserDto incorrectFieldsUserDto = new UpdateUserDto(-1L,
                "@ttt.ru", //
                "Test login", // contains a space
                "Test name",
                LocalDate.now().plusYears(10)); // future date

        final UpdateUserDto twoUser = new UpdateUserDto(2L,
                "two@tt.ru",
                "TestTwo",
                "Test name two",
                LocalDate.now().minusYears(10));

        final UpdateUserDto duplicateFieldEmailTwoUser = new UpdateUserDto(3L,
                "two@tt.ru",
                "TestThree",
                "Test name three",
                LocalDate.now().minusYears(15));

        userController.getEmailToUser().put(twoUser.getEmail(), MappingDto.mapUpdateUserDtoToUser(twoUser));

        List<ConstraintViolation<UpdateUserDto>> violations = new ArrayList<>(validator.validate(incorrectFieldsUserDto));

        List<String> listNameFields = violations.stream()
                .map(violation -> (((ConstraintViolationImpl) violation).getPropertyPath()))
                .map(pathImpl -> ((PathImpl) pathImpl).getLeafNode().getName())
                .toList();

        assertAll("incorrectFieldsUpdate",
                () -> assertEquals(4, violations.size()),
                () -> assertTrue(listNameFields.contains("id")),
                () -> assertTrue(listNameFields.contains("login")),
                () -> assertTrue(listNameFields.contains("birthday")),
                () -> assertTrue(listNameFields.contains("email")),
                () -> assertThrows(ValidationException.class,
                        () -> userController.update(incorrectFieldsUserDto)), // not found by id
                () -> assertThrows(ValidationException.class,
                        () -> userController.update(duplicateFieldEmailTwoUser))); // duplicate name
    }
}
