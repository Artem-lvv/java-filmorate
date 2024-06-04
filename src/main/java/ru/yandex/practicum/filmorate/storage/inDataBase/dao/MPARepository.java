package ru.yandex.practicum.filmorate.storage.inDataBase.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.mapper.MotionPictureAssociationRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MPARepository {
    private final MotionPictureAssociationRowMapper mapper;
    private final JdbcTemplate jdbcTemplate;

    public Optional<MPA> findById(Long id) {
        final String sqlQuery = "SELECT * FROM MOTION_PICTURE_ASSOCIATION WHERE ID = ?";
        List<MPA> query = jdbcTemplate.query(sqlQuery, mapper, id);

        if (query.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(query.get(0));
    }

    public List<MPA> findAll() {
        final String sqlQuery = "SELECT * FROM MOTION_PICTURE_ASSOCIATION";

        return jdbcTemplate.query(sqlQuery, mapper);
    }

    public Optional<MPA> findMpaByFilmId(Long filmId) {
        final String sqlQuery = "SELECT * FROM MOTION_PICTURE_ASSOCIATION " +
                "WHERE ID = (SELECT MPA_ID FROM FILM_MPA WHERE FILM_ID = ? GROUP BY MPA_ID)";
        List<MPA> query = jdbcTemplate.query(sqlQuery, mapper, filmId);

        if (query.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(query.get(0));
    }

    public void addFilmMpa(Long filmId, Long mpaId) {
        final String sqlQueryCheck = "SELECT * FROM FILM_MPA WHERE FILM_ID = ? AND MPA_ID = ?";
        List<MPA> queryCheck = jdbcTemplate.query(sqlQueryCheck, mapper, filmId, mpaId);

        if (queryCheck.isEmpty()) {
            final String sqlQuery = "INSERT INTO FILM_MPA (FILM_ID, MPA_ID) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery,
                    filmId,
                    mpaId);
        }
    }

    public void deleteFilmMpa(Long filmId) {
        final String sqlQuery = "DELETE FROM FILM_MPA WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery,
                filmId);
    }

}
