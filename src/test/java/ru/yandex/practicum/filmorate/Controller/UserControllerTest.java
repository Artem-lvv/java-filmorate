package ru.yandex.practicum.filmorate.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.converter.user.CreateUserDtoToUserConverter;
import ru.yandex.practicum.filmorate.converter.user.UpdateUserDtoToUserConverter;
import ru.yandex.practicum.filmorate.converter.user.UserToUserDtoConverter;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {FilmService.class,
        InMemoryFilmStorage.class,
        UserService.class,
        InMemoryUserStorage.class,
        UserController.class,
        CreateUserDtoToUserConverter.class,
        UserToUserDtoConverter.class,
        UpdateUserDtoToUserConverter.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    private static final String ENDPOINT_PATH = "/users";
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    InMemoryUserStorage inMemoryUserStorage;
    @Autowired
    UserService userService;

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

        inMemoryUserStorage.create(createUserDto);

        final CreateUserDto twoCreateUserDto = new CreateUserDto(2L,
                "two@aa.ru",
                "TestLoginTwo",
                "Test name two",
                birthday);

        inMemoryUserStorage.create(twoCreateUserDto);

        final CreateUserDto threeCreateUserDto = new CreateUserDto(3L,
                "three@aa.ru",
                "TestLoginThree",
                "Test name three",
                birthday);

        inMemoryUserStorage.create(threeCreateUserDto);
        ObjForTest objects = new ObjForTest(createUserDto, twoCreateUserDto, threeCreateUserDto);
        return objects;
    }

    private record ObjForTest(CreateUserDto createUserDto,
                              CreateUserDto twoCreateUserDto,
                              CreateUserDto threeCreateUserDto) {
    }

    @Test
    void addFriend_NotFound_User() throws Exception {
        ObjForTest objectsForTest = createAndGetObjForTest();

        userService.addFriend(objectsForTest.createUserDto.id(), objectsForTest.twoCreateUserDto.id());

        String notFoundIdUser = "99";

        String pathAddFriend = ENDPOINT_PATH + "/%s/friends/%s".formatted(notFoundIdUser,
                objectsForTest.twoCreateUserDto.id().toString());

        mvc.perform(put(pathAddFriend))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void addFriend_NotFound_Friend() throws Exception {
        ObjForTest objectsForTest = createAndGetObjForTest();

        userService.addFriend(objectsForTest.createUserDto.id(), objectsForTest.twoCreateUserDto.id());

        String notFoundIdFriend = "99";

        String pathAddFriend = ENDPOINT_PATH + "/%s/friends/%s"
                .formatted(objectsForTest.createUserDto.id().toString(), notFoundIdFriend);

        mvc.perform(put(pathAddFriend))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllFriendsUser_ok() throws Exception {
        ObjForTest objectsForTest = createAndGetObjForTest();

        userService.addFriend(objectsForTest.createUserDto.id(), objectsForTest.twoCreateUserDto.id());

        String pathFindAllFriends = ENDPOINT_PATH + "/%s/friends"
                .formatted(objectsForTest.createUserDto.id().toString());

        mvc.perform(get(pathFindAllFriends))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void findAllFriendsUser_NotFound_User() throws Exception {
        ObjForTest objectsForTest = createAndGetObjForTest();

        userService.addFriend(objectsForTest.createUserDto.id(), objectsForTest.twoCreateUserDto.id());

        String notFoundIdFriend = "99";

        String pathFindAllFriends = ENDPOINT_PATH + "/%s/friends"
                .formatted(notFoundIdFriend);

        mvc.perform(get(pathFindAllFriends))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFriendUser_ok() throws Exception {
        ObjForTest objectsForTest = createAndGetObjForTest();

        userService.addFriend(objectsForTest.createUserDto.id(), objectsForTest.twoCreateUserDto.id());

        String pathDeleteFriend = ENDPOINT_PATH + "/%s/friends/%s"
                .formatted(objectsForTest.createUserDto.id().toString(), objectsForTest.twoCreateUserDto.id());

        mvc.perform(delete(pathDeleteFriend))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void deleteFriendUser_NotFound_User() throws Exception {
        ObjForTest objectsForTest = createAndGetObjForTest();

        userService.addFriend(objectsForTest.createUserDto.id(), objectsForTest.twoCreateUserDto.id());

        String notFoundIdUser = "99";

        String pathDeleteFriend = ENDPOINT_PATH + "/%s/friends/%s"
                .formatted(notFoundIdUser, objectsForTest.twoCreateUserDto.id());

        mvc.perform(delete(pathDeleteFriend))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFriendUser_NotFound_Friend() throws Exception {
        ObjForTest objectsForTest = createAndGetObjForTest();

        userService.addFriend(objectsForTest.createUserDto.id(), objectsForTest.twoCreateUserDto.id());

        String notFoundIdFriend = "99";

        String pathDeleteFriend = ENDPOINT_PATH + "/%s/friends/%s"
                .formatted(objectsForTest.createUserDto.id().toString(), notFoundIdFriend);

        mvc.perform(delete(pathDeleteFriend))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getCommonFriendsUser_ok() throws Exception {
        ObjForTest objectsForTest = createAndGetObjForTest();

        userService.addFriend(objectsForTest.createUserDto.id(), objectsForTest.twoCreateUserDto.id());
        userService.addFriend(objectsForTest.twoCreateUserDto.id(), objectsForTest.threeCreateUserDto.id());

        String pathCommonFriends = ENDPOINT_PATH + "/%s/friends/common/%s"
                .formatted(objectsForTest.createUserDto.id().toString(),
                        objectsForTest.threeCreateUserDto.id().toString());

        mvc.perform(get(pathCommonFriends))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getCommonFriendsUser_NotFound_User() throws Exception {
        ObjForTest objectsForTest = createAndGetObjForTest();

        userService.addFriend(objectsForTest.createUserDto.id(), objectsForTest.twoCreateUserDto.id());
        userService.addFriend(objectsForTest.twoCreateUserDto.id(), objectsForTest.threeCreateUserDto.id());

        String notFoundIdUser = "99";

        String pathCommonFriends = ENDPOINT_PATH + "/%s/friends/common/%s"
                .formatted(notFoundIdUser, objectsForTest.threeCreateUserDto.id().toString());

        mvc.perform(get(pathCommonFriends))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getCommonFriendsUser_NotFound_OtherUser() throws Exception {
        ObjForTest objectsForTest = createAndGetObjForTest();

        userService.addFriend(objectsForTest.createUserDto.id(), objectsForTest.twoCreateUserDto.id());
        userService.addFriend(objectsForTest.twoCreateUserDto.id(), objectsForTest.threeCreateUserDto.id());

        String notFoundIdOtherUser = "99";

        String pathCommonFriends = ENDPOINT_PATH + "/%s/friends/common/%s"
                .formatted(objectsForTest.createUserDto.id(), notFoundIdOtherUser);

        mvc.perform(get(pathCommonFriends))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
