package ru.yandex.practicum.filmorate.storage.inDataBase.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.mapper.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FilmRepository {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    public Long create(CreateFilmDto createFilmDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sqlQuery = "INSERT INTO FILM (name, description, release_date, duration) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, createFilmDto.name());
            stmt.setString(2, createFilmDto.description());
            stmt.setDate(3, Date.valueOf(createFilmDto.releaseDate()));
            stmt.setInt(4, createFilmDto.duration());
            return stmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public int update(UpdateFilmDto updateFilmDto) {
        final String sqlQuery = "UPDATE FILM SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";

        return jdbcTemplate.update(sqlQuery,
                updateFilmDto.name(),
                updateFilmDto.description(),
                updateFilmDto.releaseDate(),
                updateFilmDto.duration(),
                updateFilmDto.id());
    }

    public List<Film> findAll() {
        final String sqlQuery = "SELECT * FROM FILM";
        final List<Film> films = jdbcTemplate.query(sqlQuery, filmRowMapper);

        return films;
    }

    public void addLikeFilm(Long id, Long userId) {
        final String sqlQueryCheck = "SELECT * FROM FILM_LIKES WHERE FILM_ID = ? and USER_ID = ?";
        List<Map<String, Object>> queryCheck = jdbcTemplate.queryForList(sqlQueryCheck, id, userId);

        if (queryCheck.isEmpty()) {
            final String sqlQuery = "INSERT INTO FILM_LIKES (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery,
                    id,
                    userId);
        }
    }

    public void deleteLikeFilm(Long id, Long userId) {
        final String sqlQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                id,
                userId);
    }

    public Optional<Film> findByName(String name) {
        final String sqlQuery = "SELECT * FROM FILM WHERE name = ?";
        final List<Film> films = jdbcTemplate.query(sqlQuery, filmRowMapper, name);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(films.get(0));
    }

    public Optional<Film> findById(Long id) {
        final String sqlQuery = "SELECT * FROM FILM WHERE id = ?";
        final List<Film> films = jdbcTemplate.query(sqlQuery, filmRowMapper, id);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(films.get(0));
    }

    public List<Film> findPopularFilms(Long count) {
        final String sqlQuery = "SELECT id, name, description, release_date, duration  FROM FILM AS f\n" +
                "    INNER JOIN FILM_LIKES AS fl ON f.ID = fl.FILM_ID\n" +
                "                      GROUP BY id, name, description, release_date, duration\n" +
                "                      ORDER BY COUNT(FILM_ID) DESC\n" +
                "LIMIT " + count;

        return jdbcTemplate.query(sqlQuery, filmRowMapper);
    }

    public void deleteFilm(Long filmId) {
        jdbcTemplate.update("DELETE FROM FILM WHERE id = ?", filmId);
    }
}
