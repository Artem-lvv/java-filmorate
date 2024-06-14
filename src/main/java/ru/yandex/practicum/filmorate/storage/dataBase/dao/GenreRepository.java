package ru.yandex.practicum.filmorate.storage.dataBase.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery.GenreQuery;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreRepository {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    public List<Genre> findAll() {
        return jdbcTemplate.query(GenreQuery.FIND_ALL, genreRowMapper);
    }

    public List<Genre> findById(Long id) {
        return jdbcTemplate.query(GenreQuery.FIND_BY_ID, genreRowMapper, id);
    }

    public void addFilmGenre(Long filmId, Long genreId) {
        final String sqlQueryCheck = GenreQuery.FIND_RECORD_BY_FILM_ID_AND_GENRE_ID;
        List<Genre> queryCheck = jdbcTemplate.query(sqlQueryCheck, genreRowMapper, filmId, genreId);

        if (queryCheck.isEmpty()) {
            final String sqlQuery = GenreQuery.ADD_FILM_GENRE;
            jdbcTemplate.update(sqlQuery,
                    filmId,
                    genreId);
        }
    }

    public List<Genre> findGenresByFilmId(Long filmId) {
        return jdbcTemplate.query(GenreQuery.FIND_GENRES_BY_FILM_ID, genreRowMapper, filmId);
    }

    public void deleteFilmGenre(Long filmId, Long genreId) {
        jdbcTemplate.update(GenreQuery.DELETE_FILM_GENRE,
                filmId,
                genreId);
    }

}
