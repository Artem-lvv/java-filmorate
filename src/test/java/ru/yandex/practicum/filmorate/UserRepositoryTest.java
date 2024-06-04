package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.UserRepository;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserRepository.class, UserRowMapper.class})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void create_ok() {
        ObjForTest objForTest = createAndGetObjForTest();

        Long id = userRepository.create(objForTest.createUserDto);

        Optional<User> userOptional = userRepository.findById(id);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                            assertThat(user).hasFieldOrPropertyWithValue("id",
                                    id);
                            assertThat(user).hasFieldOrPropertyWithValue("email",
                                    objForTest.createUserDto().email());
                            assertThat(user).hasFieldOrPropertyWithValue("login",
                                    objForTest.createUserDto().login());
                            assertThat(user).hasFieldOrPropertyWithValue("name",
                                    objForTest.createUserDto().name());
                            assertThat(user).hasFieldOrPropertyWithValue("birthday",
                                    objForTest.createUserDto().birthday());
                        }
                );
    }

    @Test
    @Sql("/sql/insert-data.sql")
    void update_ok() {
        long id = 1L;

        LocalDate birthdayUpdate = LocalDate.now().minusYears(5);
        String testEmailUpdate = "UPDATEttt@aa.ru";
        String testLoginUpdate = "UPDATETestLogin";
        String testNameUpdate = "UPDATE Test name";
        final UpdateUserDto updateUserDto = new UpdateUserDto(id,
                testEmailUpdate,
                testLoginUpdate,
                testNameUpdate,
                birthdayUpdate);

        int update = userRepository.update(updateUserDto);

        Optional<User> userOptional = userRepository.findById(id);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                            assertThat(user).hasFieldOrPropertyWithValue("id", id);
                            assertThat(user).hasFieldOrPropertyWithValue("email", testEmailUpdate);
                            assertThat(user).hasFieldOrPropertyWithValue("login", testLoginUpdate);
                            assertThat(user).hasFieldOrPropertyWithValue("name", testNameUpdate);
                            assertThat(user).hasFieldOrPropertyWithValue("birthday", birthdayUpdate);
                        }
                );
    }

    @Test
    void findAll_ok() {
        ObjForTest objForTest = createAndGetObjForTest();
        userRepository.create(objForTest.createUserDto);
        userRepository.create(objForTest.twoCreateUserDto);

        List<User> all = userRepository.findAll();

        assertThat(all).isNotEmpty();
    }

    @Test
    void findByEmail_ok() {
        ObjForTest objForTest = createAndGetObjForTest();

        Long id = userRepository.create(objForTest.createUserDto);

        Optional<User> userOptional = userRepository.findByEmail(objForTest.createUserDto.email());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                            assertThat(user).hasFieldOrPropertyWithValue("id",
                                    id);
                            assertThat(user).hasFieldOrPropertyWithValue("email",
                                    objForTest.createUserDto().email());
                            assertThat(user).hasFieldOrPropertyWithValue("login",
                                    objForTest.createUserDto().login());
                            assertThat(user).hasFieldOrPropertyWithValue("name",
                                    objForTest.createUserDto().name());
                            assertThat(user).hasFieldOrPropertyWithValue("birthday",
                                    objForTest.createUserDto().birthday());
                        }
                );
    }

    @Test
    void findUserById_ok() {
        ObjForTest objForTest = createAndGetObjForTest();

        Long id = userRepository.create(objForTest.createUserDto);

        Optional<User> userOptional = userRepository.findById(id);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", id)
                );
    }

    @Test
    void findByLogin_ok() {
        ObjForTest objForTest = createAndGetObjForTest();

        Long id = userRepository.create(objForTest.createUserDto);

        Optional<User> userOptional = userRepository.findByLogin(objForTest.createUserDto.login());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                            assertThat(user).hasFieldOrPropertyWithValue("id",
                                    id);
                            assertThat(user).hasFieldOrPropertyWithValue("email",
                                    objForTest.createUserDto().email());
                            assertThat(user).hasFieldOrPropertyWithValue("login",
                                    objForTest.createUserDto().login());
                            assertThat(user).hasFieldOrPropertyWithValue("name",
                                    objForTest.createUserDto().name());
                            assertThat(user).hasFieldOrPropertyWithValue("birthday",
                                    objForTest.createUserDto().birthday());
                        }
                );
    }

    @Test
    void addFriend_AndFindFriends_ok() {
        ObjForTest objForTest = createAndGetObjForTest();

        Long id = userRepository.create(objForTest.createUserDto);
        Long fiendId = userRepository.create(objForTest.twoCreateUserDto);

        userRepository.addFriend(id, fiendId);

        List<User> allFriendsUser = userRepository.findAllFriendsUser(id);
        List<Long> listId = allFriendsUser
                .stream()
                .map(User::getId)
                .toList();

        assertThat(listId).contains(fiendId);
    }

    @Test
    void deleteFriendUser_ok() {
        ObjForTest objForTest = createAndGetObjForTest();

        Long id = userRepository.create(objForTest.createUserDto);
        Long fiendId = userRepository.create(objForTest.twoCreateUserDto);

        userRepository.addFriend(id, fiendId);
        userRepository.deleteFriendUser(id, fiendId);

        List<User> allFriendsUser = userRepository.findAllFriendsUser(id);
        List<Long> listId = allFriendsUser
                .stream()
                .map(User::getId)
                .toList();

        assertThat(listId).doesNotContain(fiendId);
    }

    @Test
    void getCommonFriendsUser_ok() {
        ObjForTest objForTest = createAndGetObjForTest();

        Long id = userRepository.create(objForTest.createUserDto);
        Long fiendId = userRepository.create(objForTest.twoCreateUserDto);
        Long otherId = userRepository.create(objForTest.threeCreateUserDto);

        userRepository.addFriend(id, fiendId);
        userRepository.addFriend(otherId, fiendId);

        List<User> allFriendsUser = userRepository.getCommonFriendsUser(id, otherId);
        List<Long> listId = allFriendsUser
                .stream()
                .map(User::getId)
                .toList();

        assertThat(listId).contains(fiendId);

    }

    private ObjForTest createAndGetObjForTest() {
        LocalDate birthday = LocalDate.now().minusYears(20);
        final CreateUserDto createUserDto = new CreateUserDto(1L,
                "ttt@aa.ru",
                "TestLogin",
                "Test name",
                birthday);

        final CreateUserDto twoCreateUserDto = new CreateUserDto(2L,
                "two@aa.ru",
                "TestLoginTwo",
                "Test name two",
                birthday);


        final CreateUserDto threeCreateUserDto = new CreateUserDto(3L,
                "three@aa.ru",
                "TestLoginThree",
                "Test name three",
                birthday);

        ObjForTest objects = new ObjForTest(createUserDto, twoCreateUserDto, threeCreateUserDto);
        return objects;
    }

    private record ObjForTest(CreateUserDto createUserDto,
                              CreateUserDto twoCreateUserDto,
                              CreateUserDto threeCreateUserDto) {
    }


}