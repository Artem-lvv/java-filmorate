package ru.yandex.practicum.filmorate.storage.dataBase.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery.FilmQuery;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
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

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(FilmQuery.CREATE_FILM, new String[]{"id"});
            stmt.setString(1, createFilmDto.name());
            stmt.setString(2, createFilmDto.description());
            stmt.setDate(3, Date.valueOf(createFilmDto.releaseDate()));
            stmt.setInt(4, createFilmDto.duration());
            return stmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public int update(UpdateFilmDto updateFilmDto) {

        return jdbcTemplate.update(FilmQuery.UPDATE_FILM,
                updateFilmDto.name(),
                updateFilmDto.description(),
                updateFilmDto.releaseDate(),
                updateFilmDto.duration(),
                updateFilmDto.id());
    }

    public List<Film> findAll() {
        final List<Film> films = jdbcTemplate.query(FilmQuery.FIND_ALL, filmRowMapper);

        return films;
    }

    public void addLikeFilm(Long id, Long userId) {
        List<Map<String, Object>> queryCheck = jdbcTemplate.queryForList(FilmQuery.ADD_LIKE_FILM, id, userId);

        if (queryCheck.isEmpty()) {
            final String sqlQuery = FilmQuery.FIND_RECORD_BY_ID_AND_USER_ID;
            jdbcTemplate.update(sqlQuery,
                    id,
                    userId);
        }
    }

    public void deleteLikeFilm(Long id, Long userId) {
        jdbcTemplate.update(FilmQuery.DELETE_LIKE_FILM,
                id,
                userId);
    }

    public Optional<Film> findByName(String name) {
        final List<Film> films = jdbcTemplate.query(FilmQuery.FIND_BY_NAME, filmRowMapper, name);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(films.get(0));
    }

    public Optional<Film> findById(Long id) {
        final List<Film> films = jdbcTemplate.query(FilmQuery.FIND_BY_ID, filmRowMapper, id);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(films.get(0));
    }

    public List<Film> findPopularFilmsBySelection(Map<String, Long> allParams) {
        StringBuilder sqlQueryBuilder = new StringBuilder();
        List<Object> params = new ArrayList<>();

        FilmQuery.getQueryForFindPopularFilmsBySelection(sqlQueryBuilder, params, allParams);

        return jdbcTemplate.query(sqlQueryBuilder.toString(), params.toArray(), filmRowMapper);
    }

    public List<Long> findSimilarUsersByLikes(Long userId) {
        return jdbcTemplate.query(FilmQuery.FIND_SIMILAR_USERS_BY_LIKES, new Object[]{userId, userId},
                (rs, rowNum) -> rs.getLong("user_id"));
    }

    public List<Film> findRecommendedFilms(Long userId, Long similarUserId) {
        return jdbcTemplate.query(FilmQuery.FIND_RECOMMENDED_FILMS, filmRowMapper, similarUserId, userId);
    }

    public List<Film> findDirectorFilms(Long directorId, String sortBy) {
        String sqlQuery = FilmQuery.getQueryForFindDirectorFilms(sortBy);

        return jdbcTemplate.query(sqlQuery.toString(), filmRowMapper, directorId);
    }

    public List<Film> searchFilms(String query, String searchBy) {
        StringBuilder sqlQuery = new StringBuilder();

        List<Object> params = new ArrayList<>();

        FilmQuery.getQueryForSearchFilm(sqlQuery, query, searchBy, params);

        return jdbcTemplate.query(sqlQuery.toString(), filmRowMapper, params.toArray());
    }

    public void deleteFilm(Long filmId) {
        jdbcTemplate.update(FilmQuery.DELETE_FILM, filmId);
    }

    public Collection<Film> findAllCommonFilms(final Long userId, final Long friendId) {
        return jdbcTemplate.query(FilmQuery.FIND_ALL_COMMON_FILMS, filmRowMapper, userId, friendId);
    }
}
