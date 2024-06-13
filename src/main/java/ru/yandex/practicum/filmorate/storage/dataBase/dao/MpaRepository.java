package ru.yandex.practicum.filmorate.storage.dataBase.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery.MpaQuery;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaRepository {
    private final MpaRowMapper mapper;
    private final JdbcTemplate jdbcTemplate;

    public Optional<MPA> findById(Long id) {
        List<MPA> query = jdbcTemplate.query(MpaQuery.FIND_BY_ID, mapper, id);

        if (query.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(query.get(0));
    }

    public List<MPA> findAll() {
        return jdbcTemplate.query(MpaQuery.FIND_ALL, mapper);
    }

    public Optional<MPA> findMpaByFilmId(Long filmId) {
        List<MPA> query = jdbcTemplate.query(MpaQuery.FIND_MPA_BY_FILM_ID, mapper, filmId);

        if (query.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(query.get(0));
    }

    public void addFilmMpa(Long filmId, Long mpaId) {
        List<MPA> queryCheck = jdbcTemplate.query(MpaQuery.FIND_RECORD_BY_FILM_ID_AND_MPA_ID, mapper, filmId, mpaId);

        if (queryCheck.isEmpty()) {
            final String sqlQuery = MpaQuery.ADD_FILM_MPA;
            jdbcTemplate.update(sqlQuery,
                    filmId,
                    mpaId);
        }
    }

    public void deleteFilmMpa(Long filmId) {
        jdbcTemplate.update(MpaQuery.DELETE_FILM_MPA,
                filmId);
    }

}
