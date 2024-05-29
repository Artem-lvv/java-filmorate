package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.UserRepository;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.mapper.UserRowMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {UserRepository.class, UserRowMapper.class})
class FilmorateApplicationTests2 {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql("/sql/insert-data.sql")
    void testFindUserById() {
        Optional<User> userOptional = userRepository.findById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }
}