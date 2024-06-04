package ru.yandex.practicum.filmorate.storage.inDataBase.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.mapper.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public Long create(CreateUserDto createUserDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sqlQuery = "INSERT INTO USERS (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, createUserDto.email());
            stmt.setString(2, createUserDto.login());
            stmt.setString(3, createUserDto.name());
            stmt.setDate(4, Date.valueOf(createUserDto.birthday()));
            return stmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public int update(UpdateUserDto updateUserDto) {
        final String sqlQuery = "UPDATE USERS SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

        return jdbcTemplate.update(sqlQuery,
                updateUserDto.email(),
                updateUserDto.login(),
                updateUserDto.name(),
                updateUserDto.birthday(),
                updateUserDto.id());
    }

    public List<User> findAll() {
        final String sqlQuery = "SELECT * FROM USERS ORDER BY ID";
        final List<User> users = jdbcTemplate.query(sqlQuery, userRowMapper);

        return users;
    }

    public Optional<User> findByEmail(String email) {
        final String sqlQuery = "SELECT * FROM USERS WHERE email = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, userRowMapper, email);

        if (users.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(users.get(0));
    }

    public Optional<User> findById(Long id) {
        final String sqlQuery = "SELECT * FROM USERS WHERE id = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, userRowMapper, id);

        if (users.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(users.get(0));
    }

    public Optional<User> findByLogin(String login) {
        final String sqlQuery = "SELECT * FROM USERS WHERE login = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, userRowMapper, login);

        if (users.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(users.get(0));
    }

    public void addFriend(Long userId, Long friendId) {
        final String sqlQueryCheck = "SELECT * FROM USER_FRIENDS WHERE USER_ID = ? and FRIEND_ID = ?";
        List<Map<String, Object>> queryCheck = jdbcTemplate.queryForList(sqlQueryCheck, userId, friendId);

        if (queryCheck.isEmpty()) {
            final String sqlQuery = "INSERT INTO USER_FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery,
                    userId,
                    friendId);
        }
    }

    public List<User> findAllFriendsUser(Long userId) {
        final String sqlQueryCheck = "SELECT * FROM USERS " +
                "WHERE ID IN (SELECT FRIEND_ID FROM USER_FRIENDS WHERE USER_ID = ?)";

        return jdbcTemplate.query(sqlQueryCheck, userRowMapper, userId);
    }

    public void deleteFriendUser(Long userId, Long friendId) {
        final String sqlQuery = "DELETE FROM USER_FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery,
                userId,
                friendId);
    }

    public List<User> getCommonFriendsUser(Long userId, Long otherId) {
        final String sqlQuery = "SELECT *\n" +
                "FROM USERS\n" +
                "WHERE ID IN (\n" +
                "    SELECT uf1.friend_id\n" +
                "    FROM USER_FRIENDS uf1\n" +
                "             JOIN USER_FRIENDS uf2 ON uf1.friend_id = uf2.friend_id\n" +
                "    WHERE uf1.user_id = ? AND uf2.user_id = ?\n" +
                "    )";
        List<User> query = jdbcTemplate.query(sqlQuery, userRowMapper, userId, otherId);

        return query;
    }
}
