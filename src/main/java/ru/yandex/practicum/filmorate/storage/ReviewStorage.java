package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.model.review.dto.CreateReviewDto;
import ru.yandex.practicum.filmorate.model.review.dto.UpdateReviewDto;

import java.util.Collection;

public interface ReviewStorage {

    Review create(final CreateReviewDto createReviewDto);

    Review update(final UpdateReviewDto updateReviewDto);

    void remove(final Long id);

    Review findByIdOrElseThrow(final Long id);

    Collection<Review> findAll(final Long filmId, final int count);

    Review addLike(final Long reviewId, final Long userId);

    Review addDislike(final Long reviewId, final Long userId);

    Review removeLike(final Long reviewId, final Long userId);

    Review removeDislike(final Long reviewId, final Long userId);
}
