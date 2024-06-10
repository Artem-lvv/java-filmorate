package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.model.review.dto.CreateReviewDto;
import ru.yandex.practicum.filmorate.model.review.dto.UpdateReviewDto;

import java.util.Collection;

public interface ReviewStorage {

    Review create(final CreateReviewDto createReviewDto);

    Review update(final UpdateReviewDto updateReviewDto);

    void remove(final long id);

    Review findByIdOrElseThrow(final long id);

    Collection<Review> findAll(final Long filmId, final int count);

    Review addLike(final long reviewId, final long userId);

    Review addDislike(final long reviewId, final long userId);

    Review removeLike(final long reviewId, final long userId);

    Review removeDislike(final long reviewId, final long userId);
}
