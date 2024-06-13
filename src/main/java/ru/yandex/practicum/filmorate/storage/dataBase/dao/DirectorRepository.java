package ru.yandex.practicum.filmorate.storage.dataBase.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.dto.DirectorDto;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery.DirectorQuery;

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
        return jdbcTemplate.query(DirectorQuery.FIND_ALL, directorRowMapper);
    }

    public Optional<Director> findById(Long id) {
        List<Director> directors = jdbcTemplate.query(DirectorQuery.FIND_BY_ID, directorRowMapper, id);

        return directors.isEmpty() ? Optional.empty() : Optional.ofNullable(directors.get(0));
    }

    public Long create(DirectorDto createDirectorDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(DirectorQuery.CREATE_DIRECTOR, new String[]{"id"});
            stmt.setString(1, createDirectorDto.name());
            return stmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public int update(DirectorDto directorDto) {
        return jdbcTemplate.update(DirectorQuery.UPDATE_DIRECTOR, directorDto.name(), directorDto.id());
    }

    public void addFilmDirector(Long filmId, Long directorId) {
        List<Director> queryCheck = jdbcTemplate.query(DirectorQuery.FIND_RECORD_BY_FILM_ID_AND_DIRECTOR_ID,
                directorRowMapper, filmId, directorId);

        if (queryCheck.isEmpty()) {
            jdbcTemplate.update(DirectorQuery.ADD_FILM_DIRECTOR, filmId, directorId);
        }
    }

    public List<Director> findDirectorsByFilmId(Long filmId) {
        return jdbcTemplate.query(DirectorQuery.FIND_DIRECTORS_BY_FILM_ID, directorRowMapper, filmId);
    }

    public void deleteDirector(Long id) {
        jdbcTemplate.update(DirectorQuery.DELETE_DIRECTOR, id);
    }

    public void deleteFilmDirector(Long filmId, Long directorId) {
        jdbcTemplate.update(DirectorQuery.DELETE_FILM_DIRECTOR, filmId, directorId);
    }

    public void updateFilmDirectors(Long filmId, Set<Long> directorIDs) {
        jdbcTemplate.update(DirectorQuery.DELETE_DIRECTOR_BY_FILM_ID, filmId);

        jdbcTemplate.batchUpdate(DirectorQuery.UPDATE_FILM_DIRECTORS, new BatchPreparedStatementSetter() {
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
