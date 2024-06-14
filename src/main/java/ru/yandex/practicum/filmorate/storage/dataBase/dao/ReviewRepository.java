package ru.yandex.practicum.filmorate.storage.dataBase.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.model.review.dto.CreateReviewDto;
import ru.yandex.practicum.filmorate.model.review.dto.UpdateReviewDto;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery.ReviewQuery;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {
    private final JdbcTemplate jdbc;
    private final ReviewRowMapper reviewRowMapper;

    public Optional<Review> findById(final Long id) {
        try {
            final Review review = jdbc.queryForObject(ReviewQuery.FIND_BY_ID_QUERY, reviewRowMapper, id);
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public Long create(final CreateReviewDto createReviewDto) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ReviewQuery.INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, createReviewDto.content());
            ps.setBoolean(2, createReviewDto.isPositive());
            ps.setLong(3, createReviewDto.userId());
            ps.setLong(4, createReviewDto.filmId());
            return ps;
        }, keyHolder);

        final Long id = keyHolder.getKeyAs(Long.class);

        if (Objects.nonNull(id)) {
            return id;
        }

        throw new InternalServerException("Failed to save data");
    }

    public void update(final UpdateReviewDto updateReviewDto) {
        final int rowsUpdated = jdbc.update(
                ReviewQuery.UPDATE_QUERY,
                updateReviewDto.content(),
                updateReviewDto.isPositive(),
                updateReviewDto.reviewId()
        );

        checkRowsUpdated(rowsUpdated, ReviewQuery.FAILED_TO_UPDATE_DATA);
    }

    public void delete(final Long id) {
        final int rowsUpdated = jdbc.update(ReviewQuery.DELETE_QUERY, id);

        checkRowsUpdated(rowsUpdated, ReviewQuery.FAILED_TO_DELETE_DATA);
    }

    public Collection<Review> findAll() {
        return jdbc.query(ReviewQuery.FIND_ALL_AND_SORT_BY_USEFUL_QUERY, reviewRowMapper);
    }

    public Collection<Review> findMany(final int count) {
        return jdbc.query(ReviewQuery.FIND_ALL_AND_SORT_BY_USEFUL_WITH_COUNT_QUERY, reviewRowMapper, count);
    }

    public Collection<Review> findMany(final Long filmId, final int count) {
        return jdbc.query(ReviewQuery.FIND_ALL_BY_ID_FILM_AND_SORT_BY_USEFUL_QUERY, reviewRowMapper, filmId, count);
    }

    public Long getUseful(final Long reviewId) {
        return jdbc.queryForObject(ReviewQuery.FIND_BY_ID_USEFUL_QUERY, Long.class, reviewId);
    }

    public void updateUseful(final Long reviewId, final long useful) {
        final int rowsUpdated = jdbc.update(ReviewQuery.UPDATE_USEFUL_QUERY, useful, reviewId);

        checkRowsUpdated(rowsUpdated, ReviewQuery.FAILED_TO_UPDATE_DATA);
    }

    public void saveLike(final Long reviewId, final Long userId, final boolean isPositive) {
        final int rowsUpdated = jdbc.update(ReviewQuery.INSERT_LIKE_QUERY, reviewId, userId, isPositive);

        checkRowsUpdated(rowsUpdated, ReviewQuery.FAILED_TO_UPDATE_DATA);
    }

    public void removeLike(final Long reviewId, final Long userId) {
        final int rowsUpdated = jdbc.update(ReviewQuery.DELETE_LIKE_QUERY, reviewId, userId);

        checkRowsUpdated(rowsUpdated, ReviewQuery.FAILED_TO_UPDATE_DATA);
    }

    private void checkRowsUpdated(final int rowsUpdated, final String action) {
        if (rowsUpdated == 0) {
            throw new InternalServerException(action);
        }
    }
}
