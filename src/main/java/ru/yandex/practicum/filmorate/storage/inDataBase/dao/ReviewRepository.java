package ru.yandex.practicum.filmorate.storage.inDataBase.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.model.review.dto.CreateReviewDto;
import ru.yandex.practicum.filmorate.model.review.dto.UpdateReviewDto;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.mapper.ReviewRowMapper;

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
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO reviews (content, is_positive, user_id, film_id) "
            + "VALUES (?, ?, ?, ?)";
    /*    private static final String UPDATE_QUERY = "UPDATE reviews SET content = ?, is_positive = ?, useful = ?"
                + "WHERE review_id = ? AND user_id = ? AND film_id = ?";*/
    private static final String UPDATE_QUERY = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
    private static final String FIND_ALL_AND_SORT_BY_USEFUL_QUERY = "SELECT * FROM reviews ORDER BY useful DESC";
    private static final String FIND_ALL_BY_ID_FILM_AND_SORT_BY_USEFUL_QUERY = "SELECT * FROM reviews WHERE film_id = ?"
            + "ORDER BY useful DESC LIMIT ?";
    private static final String FIND_ALL_AND_SORT_BY_USEFUL_WITH_COUNT_QUERY = "SELECT * FROM reviews "
            + "ORDER BY useful DESC LIMIT ?";
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    private static final String UPDATE_USEFUL_QUERY = "UPDATE reviews SET useful = ? WHERE review_id = ?";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO likes_review (review_id, user_id, is_positive) "
            + "VALUES (?, ?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes_review WHERE review_id = ? AND user_id = ?";
    private static final String FIND_BY_ID_USEFUL_QUERY = "SELECT useful FROM reviews WHERE review_id = ?";
    private static final String FAILED_TO_UPDATE_DATA = "Failed to update data";
    private static final String FAILED_TO_DELETE_DATA = "Failed to update data";

    public Optional<Review> findById(final Long id) {
        try {
            final Review review = jdbc.queryForObject(FIND_BY_ID_QUERY, reviewRowMapper, id);
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public Long create(final CreateReviewDto createReviewDto) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
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
                UPDATE_QUERY,
                updateReviewDto.content(),
                updateReviewDto.isPositive(),
                updateReviewDto.reviewId()
        );

        checkRowsUpdated(rowsUpdated, FAILED_TO_UPDATE_DATA);
    }

    public void delete(final Long id) {
        final int rowsUpdated = jdbc.update(DELETE_QUERY, id);

        checkRowsUpdated(rowsUpdated, FAILED_TO_DELETE_DATA);
    }

    public Collection<Review> findAll() {
        return jdbc.query(FIND_ALL_AND_SORT_BY_USEFUL_QUERY, reviewRowMapper);
    }

    public Collection<Review> findMany(final int count) {
        return jdbc.query(FIND_ALL_AND_SORT_BY_USEFUL_WITH_COUNT_QUERY, reviewRowMapper, count);
    }

    public Collection<Review> findMany(final Long filmId, final int count) {
        return jdbc.query(FIND_ALL_BY_ID_FILM_AND_SORT_BY_USEFUL_QUERY, reviewRowMapper, filmId, count);
    }

    public Long getUseful(final Long reviewId) {
        return jdbc.queryForObject(FIND_BY_ID_USEFUL_QUERY, Long.class, reviewId);
    }

    public void updateUseful(final Long reviewId, final long useful) {
        final int rowsUpdated = jdbc.update(UPDATE_USEFUL_QUERY, useful, reviewId);

        checkRowsUpdated(rowsUpdated, FAILED_TO_UPDATE_DATA);
    }

    public void saveLike(final Long reviewId, final Long userId, final boolean isPositive) {
        final int rowsUpdated = jdbc.update(INSERT_LIKE_QUERY, reviewId, userId, isPositive);

        checkRowsUpdated(rowsUpdated, FAILED_TO_UPDATE_DATA);
    }

    public void removeLike(final Long reviewId, final Long userId) {
        final int rowsUpdated = jdbc.update(DELETE_LIKE_QUERY, reviewId, userId);

        checkRowsUpdated(rowsUpdated, FAILED_TO_UPDATE_DATA);
    }

    private void checkRowsUpdated(final int rowsUpdated, final String action) {
        if (rowsUpdated == 0) {
            throw new InternalServerException(action);
        }
    }
}
