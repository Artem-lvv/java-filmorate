package ru.yandex.practicum.filmorate.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    private static final String ENDPOINT_PATH = "/users";
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void should_return_all_users() throws Exception {
        mvc.perform(get(ENDPOINT_PATH))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void create_Ok() throws Exception {
        LocalDate birthday = LocalDate.now().minusYears(20);
        final CreateUserDto createUserDto = new CreateUserDto(1L,
                "ttt@aa.ru",
                "TestLogin",
                "Test name",
                birthday);

        createUser(createUserDto)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.id").value(1),
                        jsonPath("$.email").value("ttt@aa.ru"),
                        jsonPath("$.login").value("TestLogin"),
                        jsonPath("$.name").value("Test name"),
                        jsonPath("$.birthday").value(birthday.toString())
                );
    }

    @Test
    void create_BadRequest_incorrect_Fields() throws Exception {
        LocalDate birthdayFuture = LocalDate.now().plusYears(20);
        final CreateUserDto createUserDto = new CreateUserDto(1L,
                "@@@ttt.aaru",
                "Test  Login",
                "Test name",
                birthdayFuture);


        createUser(createUserDto)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private ResultActions createUser(CreateUserDto createUser) throws Exception {
        return mvc.perform(post(ENDPOINT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUser)));
    }

    @Test
    void create_Conflict_Duplicate_Email() throws Exception {
        LocalDate birthday = LocalDate.now().minusYears(20);
        final CreateUserDto createUserDto = new CreateUserDto(1L,
                "ttt@aa.ru",
                "TestLogin",
                "Test name",
                birthday);

        createUser(createUserDto);

        final CreateUserDto createUserDtoDuplicateEmail = new CreateUserDto(1L,
                "ttt@aa.ru",
                "TestLoginDuplicate",
                "Test name duplicate",
                birthday);

        createUser(createUserDtoDuplicateEmail)
                .andDo(print())
                .andExpect(status().isConflict()); // duplicate name
    }

    @Test
    void update_Ok() throws Exception {
        LocalDate birthday = LocalDate.now().minusYears(20);
        final CreateUserDto createUserDto = new CreateUserDto(1L,
                "ttt@aa.ru",
                "TestLogin",
                "Test name",
                birthday);

        createUser(createUserDto);

        LocalDate updateBirthday = birthday.minusYears(10);
        final UpdateUserDto updateUserDto = new UpdateUserDto(1L,
                "Update@aa.ru",
                "TestLoginUpdate",
                "Test name update",
                updateBirthday);

        updateUser(updateUserDto)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(1),
                        jsonPath("$.email").value("Update@aa.ru"),
                        jsonPath("$.login").value("TestLoginUpdate"),
                        jsonPath("$.name").value("Test name update"),
                        jsonPath("$.birthday").value(updateBirthday.toString()));
    }


    private ResultActions updateUser(UpdateUserDto updateUserDto) throws Exception {
        return mvc.perform(put(ENDPOINT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserDto)));
    }

    @Test
    void update_NotFound_By_id() throws Exception {
        LocalDate birthday = LocalDate.now().minusYears(20);
        final CreateUserDto createUserDto = new CreateUserDto(1L,
                "ttt@aa.ru",
                "TestLogin",
                "Test name",
                birthday);

        createUser(createUserDto);

        LocalDate updateBirthday = birthday.minusYears(10);
        final UpdateUserDto updateUserDto = new UpdateUserDto(Long.MAX_VALUE,
                "Update@aa.ru",
                "TestLoginUpdate",
                "Test name update",
                updateBirthday);

        updateUser(updateUserDto)
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void update_Conflict_Duplicate_Field_Email() throws Exception {
        LocalDate birthday = LocalDate.now().minusYears(20);
        final CreateUserDto createUserDto = new CreateUserDto(1L,
                "ttt@aa.ru",
                "TestLogin",
                "Test name",
                birthday);

        createUser(createUserDto);

        final CreateUserDto twoCreateUserDto = new CreateUserDto(2L,
                "two@aa.ru",
                "TestLoginTwo",
                "Test name two",
                birthday);

        createUser(twoCreateUserDto);

        final UpdateUserDto updateUserDtoDuplicateEmail = new UpdateUserDto(1L,
                "two@aa.ru",
                "TestLoginDuplicate",
                "Test name duplicate",
                birthday);

        updateUser(updateUserDtoDuplicateEmail)
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void update_Conflict_Duplicate_Field_Login() throws Exception {
        LocalDate birthday = LocalDate.now().minusYears(20);
        final CreateUserDto createUserDto = new CreateUserDto(1L,
                "ttt@aa.ru",
                "TestLogin",
                "Test name",
                birthday);

        createUser(createUserDto);

        final CreateUserDto twoCreateUserDto = new CreateUserDto(2L,
                "two@aa.ru",
                "TestLoginTwo",
                "Test name two",
                birthday);

        createUser(twoCreateUserDto);

        final UpdateUserDto updateUserDtoDuplicateLogin = new UpdateUserDto(1L,
                "ttt@aa.ru",
                "TestLoginTwo",
                "Test name duplicate",
                birthday);

        updateUser(updateUserDtoDuplicateLogin)
                .andDo(print())
                .andExpect(status().isConflict());

    }

}
