package ru.yandex.practicum.filmorate.storage.inDataBase.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.mapper.GenreRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreRepository {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    public List<Genre> findAll() {
        final String sqlQuery = "SELECT * FROM GENRE";

        return jdbcTemplate.query(sqlQuery, genreRowMapper);
    }

    public List<Genre> findById(Long id) {
        final String sqlQuery = "SELECT * FROM GENRE WHERE ID = ?";
        return jdbcTemplate.query(sqlQuery, genreRowMapper, id);
    }

    public void addFilmGenre(Long filmId, Long genreId) {
        final String sqlQueryCheck = "SELECT * FROM FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";
        List<Genre> queryCheck = jdbcTemplate.query(sqlQueryCheck, genreRowMapper, filmId, genreId);

        if (queryCheck.isEmpty()) {
            final String sqlQuery = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery,
                    filmId,
                    genreId);
        }
    }

    public List<Genre> findGenresByFilmId(Long filmId) {
        final String sqlQueryCheck = "SELECT * FROM GENRE" +
                " WHERE ID IN (SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ? GROUP BY GENRE_ID) ORDER BY ID";
        return jdbcTemplate.query(sqlQueryCheck, genreRowMapper, filmId);
    }

    public void deleteFilmGenre(Long filmId, Long genreId) {
        final String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";
        jdbcTemplate.update(sqlQuery,
                filmId,
                genreId);
    }

}
