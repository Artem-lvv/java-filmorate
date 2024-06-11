package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.exception.EntityDuplicateException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor
class UserControllerTest {
    private static final String ENDPOINT_PATH = "/users";

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private FilmStorage filmStorage;

    @MockBean(name = "userService")
    private UserStorage userStorage;

    @MockBean(name = "feedService")
    private FeedService feedService;

    @Qualifier("mvcConversionService")
    private final ConversionService cs;

    @Test
    void should_return_all_users() throws Exception {
        List<UserDto> usersList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            usersList.add(UserDto.builder()
                    .id((long) i)
                    .name("test name " + i)
                    .login("testLogin" + i)
                    .email("testEmail@" + i)
                    .birthday(LocalDate.now())
                    .build());
        }

        when(userStorage.findAll())
                .thenReturn(usersList);

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

        User user = cs.convert(createUserDto, User.class);
        UserDto userDto = cs.convert(user, UserDto.class);

        when(userStorage.create(createUserDto))
                .thenReturn(userDto);

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

        String testId = "99";
        doThrow(new EntityDuplicateException("user", testId))
                .when(userStorage)
                .create(any());

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

        String testId = "99";
        doThrow(new EntityDuplicateException("user", testId))
                .when(userStorage)
                .create(any());

        createUser(createUserDto)
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

        User user = cs.convert(updateUserDto, User.class);
        UserDto userDto = cs.convert(user, UserDto.class);

        when(userStorage.update(updateUserDto))
                .thenReturn(userDto);

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

        long notFoundIdUser = 88L;
        doThrow(new EntityNotFoundByIdException("user", String.valueOf(notFoundIdUser)))
                .when(userStorage)
                .update(any());

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

        doThrow(new EntityDuplicateException("field name", "email value"))
                .when(userStorage)
                .update(any());

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

        doThrow(new EntityDuplicateException("field name", "name value"))
                .when(userStorage)
                .update(any());

        updateUser(updateUserDtoDuplicateLogin)
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void addFriend_ok() throws Exception {
        ObjForTest objectsForTest = createAndGetObjForTest();

        String pathAddFriend = ENDPOINT_PATH + "/%s/friends/%s".formatted(objectsForTest.createUserDto().id().toString(),
                objectsForTest.twoCreateUserDto().id().toString());

        mvc.perform(put(pathAddFriend))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private ObjForTest createAndGetObjForTest() {
        LocalDate birthday = LocalDate.now().minusYears(20);
        final CreateUserDto createUserDto = new CreateUserDto(1L,
                "ttt@aa.ru",
                "TestLogin",
                "Test name",
                birthday);

        userStorage.create(createUserDto);

        final CreateUserDto twoCreateUserDto = new CreateUserDto(2L,
                "two@aa.ru",
                "TestLoginTwo",
                "Test name two",
                birthday);

        userStorage.create(twoCreateUserDto);

        final CreateUserDto threeCreateUserDto = new CreateUserDto(3L,
                "three@aa.ru",
                "TestLoginThree",
                "Test name three",
                birthday);

        userStorage.create(threeCreateUserDto);
        ObjForTest objects = new ObjForTest(createUserDto, twoCreateUserDto, threeCreateUserDto);
        return objects;
    }

    private record ObjForTest(CreateUserDto createUserDto,
                              CreateUserDto twoCreateUserDto,
                              CreateUserDto threeCreateUserDto) {
    }

    @Test
    void findAllFriendsUser_ok() throws Exception {
        ObjForTest objectsForTest = createAndGetObjForTest();

        userStorage.addFriend(objectsForTest.createUserDto.id(), objectsForTest.twoCreateUserDto.id());

        String pathFindAllFriends = ENDPOINT_PATH + "/%s/friends"
                .formatted(objectsForTest.createUserDto.id().toString());

        mvc.perform(get(pathFindAllFriends))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void findAllFriendsUser_NotFound_User() throws Exception {
        long notFoundIdUser = 88L;
        String pathFindAllFriends = ENDPOINT_PATH + "/%s/friends"
                .formatted(notFoundIdUser);

        doThrow(new EntityNotFoundByIdException("user", String.valueOf(notFoundIdUser)))
                .when(userStorage)
                .findAllFriendsUser(notFoundIdUser);

        mvc.perform(get(pathFindAllFriends))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFriendUser_ok() throws Exception {
        long idUser = 88L;
        long idFriend = 99L;
        String pathDeleteFriend = ENDPOINT_PATH + "/%s/friends/%s"
                .formatted(idUser, idFriend);

        mvc.perform(delete(pathDeleteFriend))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void deleteFriendUser_NotFound_User() throws Exception {
        long notFoundIdUser = 88L;
        long notFoundIdFriend = 99L;

        String pathDeleteFriend = ENDPOINT_PATH + "/%s/friends/%s"
                .formatted(notFoundIdUser, notFoundIdFriend);

        doThrow(new EntityNotFoundByIdException("user", String.valueOf(notFoundIdUser)))
                .when(userStorage)
                .deleteFriendUser(notFoundIdUser, notFoundIdFriend);

        mvc.perform(delete(pathDeleteFriend))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFriendUser_NotFound_Friend() throws Exception {
        long idUser = 88L;
        long idFriend = 99L;

        String pathDeleteFriend = ENDPOINT_PATH + "/%s/friends/%s"
                .formatted(idUser, idFriend);

        doThrow(new EntityNotFoundByIdException("user", String.valueOf(idUser)))
                .when(userStorage)
                .deleteFriendUser(idUser, idFriend);

        mvc.perform(delete(pathDeleteFriend))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getCommonFriendsUser_ok() throws Exception {
        long idUser = 88L;
        long idFriend = 99L;

        String pathCommonFriends = ENDPOINT_PATH + "/%s/friends/common/%s"
                .formatted(idUser, idFriend);

        List<UserDto> usersList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            usersList.add(UserDto.builder()
                    .id((long) i)
                    .name("test name " + i)
                    .login("testLogin" + i)
                    .email("testEmail@" + i)
                    .birthday(LocalDate.now())
                    .build());
        }

        when(userStorage.getCommonFriendsUser(idUser, idFriend))
                .thenReturn(usersList);

        mvc.perform(get(pathCommonFriends))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getCommonFriendsUser_NotFound_User() throws Exception {
        long notFoundIdUser = 88L;
        long notFoundIdFriend = 99L;

        String pathCommonFriends = ENDPOINT_PATH + "/%s/friends/common/%s"
                .formatted(notFoundIdUser, notFoundIdFriend);

        doThrow(new EntityNotFoundByIdException("user", String.valueOf(notFoundIdUser)))
                .when(userStorage)
                .getCommonFriendsUser(notFoundIdUser, notFoundIdFriend);

        mvc.perform(get(pathCommonFriends))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getCommonFriendsUser_NotFound_OtherUser() throws Exception {
        long notFoundIdUser = 88L;
        long notFoundIdFriend = 99L;

        String pathCommonFriends = ENDPOINT_PATH + "/%s/friends/common/%s"
                .formatted(notFoundIdUser, notFoundIdFriend);

        doThrow(new EntityNotFoundByIdException("user", String.valueOf(notFoundIdUser)))
                .when(userStorage)
                .getCommonFriendsUser(notFoundIdUser, notFoundIdFriend);

        mvc.perform(get(pathCommonFriends))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
