package ru.yandex.practicum.filmorate.storage.dataBase.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery.UserQuery;

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

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(UserQuery.CREATE_USER, new String[]{"id"});
            stmt.setString(1, createUserDto.email());
            stmt.setString(2, createUserDto.login());
            stmt.setString(3, createUserDto.name());
            stmt.setDate(4, Date.valueOf(createUserDto.birthday()));
            return stmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public int update(UpdateUserDto updateUserDto) {
        return jdbcTemplate.update(UserQuery.UPDATE_USER,
                updateUserDto.email(),
                updateUserDto.login(),
                updateUserDto.name(),
                updateUserDto.birthday(),
                updateUserDto.id());
    }

    public List<User> findAll() {
        final List<User> users = jdbcTemplate.query(UserQuery.FIND_ALL, userRowMapper);

        return users;
    }

    public Optional<User> findByEmail(String email) {
        final List<User> users = jdbcTemplate.query(UserQuery.FIND_BY_EMAIL, userRowMapper, email);

        if (users.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(users.get(0));
    }

    public Optional<User> findById(Long id) {
        final List<User> users = jdbcTemplate.query(UserQuery.FIND_BY_ID, userRowMapper, id);

        if (users.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(users.get(0));
    }

    public Optional<User> findByLogin(String login) {
        final List<User> users = jdbcTemplate.query(UserQuery.FIND_BY_LOGIN, userRowMapper, login);

        if (users.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(users.get(0));
    }

    public void addFriend(Long userId, Long friendId) {
        List<Map<String, Object>> queryCheck = jdbcTemplate.queryForList(UserQuery.FIND_RECOED_BY_USER_ID_AND_FRIEND_ID,
                userId, friendId);

        if (queryCheck.isEmpty()) {
            final String sqlQuery = UserQuery.ADD_FRIEND;
            jdbcTemplate.update(sqlQuery,
                    userId,
                    friendId);
        }
    }

    public List<User> findAllFriendsUser(Long userId) {
        return jdbcTemplate.query(UserQuery.FIND_ALL_FRIENDS_USER, userRowMapper, userId);
    }

    public void deleteFriendUser(Long userId, Long friendId) {
        jdbcTemplate.update(UserQuery.DELETE_FRIEND_USER,
                userId,
                friendId);
    }

    public List<User> getCommonFriendsUser(Long userId, Long otherId) {
        List<User> query = jdbcTemplate.query(UserQuery.GET_COMMON_FRIENDS_USER, userRowMapper, userId, otherId);

        return query;
    }

    public void deleteUser(Long userId) {
        jdbcTemplate.update(UserQuery.DELETE_USER, userId);
    }
}
