package ru.yandex.practicum.filmorate.storage.inDataBase.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.dto.DirectorDto;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.mapper.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class DirectorRepository {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMapper directorRowMapper;

    public List<Director> findAll() {
        final String sqlQuery = "SELECT * FROM DIRECTOR";
        return jdbcTemplate.query(sqlQuery, directorRowMapper);
    }

    public Optional<Director> findById(Long id) {
        final String sqlQuery = "SELECT * FROM DIRECTOR WHERE ID = ?";
        List<Director> directors = jdbcTemplate.query(sqlQuery, directorRowMapper, id);

        return directors.isEmpty() ? Optional.empty() : Optional.ofNullable(directors.get(0));
    }

    public Long create(DirectorDto createDirectorDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sqlQuery = "INSERT INTO DIRECTOR (name) VALUES (?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, createDirectorDto.name());
            return stmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public int update(DirectorDto directorDto) {
        final String sqlQuery = "UPDATE DIRECTOR SET name = ? WHERE id = ?";

        return jdbcTemplate.update(sqlQuery, directorDto.name(), directorDto.id());
    }

    public void addFilmDirector(Long filmId, Long directorId) {
        final String sqlQueryCheck = "SELECT * FROM FILM_DIRECTOR WHERE FILM_ID = ? AND DIRECTOR_ID = ?";
        List<Director> queryCheck = jdbcTemplate.query(sqlQueryCheck, directorRowMapper, filmId, directorId);

        if (queryCheck.isEmpty()) {
            final String sqlQuery = "INSERT INTO FILM_DIRECTOR (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, filmId, directorId);
        }
    }

    public List<Director> findDirectorsByFilmId(Long filmId) {
        final String
                sqlQueryCheck =
                "SELECT * FROM DIRECTOR" +
                        " WHERE ID IN (SELECT DIRECTOR_ID FROM FILM_DIRECTOR WHERE FILM_ID = ?) ORDER BY ID";
        return jdbcTemplate.query(sqlQueryCheck, directorRowMapper, filmId);
    }

    public void deleteDirector(Long id) {
        final String sqlQuery = "DELETE FROM DIRECTOR WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, id);

    }

    public void deleteFilmDirector(Long filmId, Long directorId) {
        final String sqlQuery = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ? AND DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId, directorId);

    }

    public void updateFilmDirectors(Long filmId, Set<Long> directorIDs) {
        String sqlDelete = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?;";
        jdbcTemplate.update(sqlDelete, filmId);

        String sqlInsert = "INSERT INTO FILM_DIRECTOR(FILM_ID, DIRECTOR_ID) VALUES(?, ?);";
        jdbcTemplate.batchUpdate(sqlInsert, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setLong(1, filmId);
                preparedStatement.setLong(2, directorIDs.stream().toList().get(i));
            }

            @Override
            public int getBatchSize() {
                return directorIDs.size();
            }
        });
    }

}
