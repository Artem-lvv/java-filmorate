package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.model.review.dto.CreateReviewDto;
import ru.yandex.practicum.filmorate.model.review.dto.UpdateReviewDto;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.FilmRepository;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.ReviewRepository;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.UserRepository;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.model.feed.EventType.*;
import static ru.yandex.practicum.filmorate.model.feed.Operation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService implements ReviewStorage {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final FeedService feedService;

    @Override
    @Transactional
    public Review create(final CreateReviewDto createReviewDto) {
        userRepository.findById(createReviewDto.userId())
                .orElseThrow(() -> new EntityNotFoundByIdException("review", createReviewDto.toString()));

        filmRepository.findById(createReviewDto.filmId())
                .orElseThrow(() -> new EntityNotFoundByIdException("review", createReviewDto.toString()));

        Review review = cs.convert(createReviewDto, Review.class);
        final Long id = reviewRepository.create(createReviewDto);
        review.setReviewId(id);

        log.info("Create {}", review);
        feedService.addFeed(Feed.builder()
                .entityId(review.getReviewId())
                .userId(review.getUserId())
                .eventType(REVIEW)
                .operation(ADD)
                .build());

        return review;
    }

    @Override
    public Review update(final UpdateReviewDto updateReviewDto) {
        userRepository.findById(updateReviewDto.userId())
                .orElseThrow(() -> new EntityNotFoundByIdException("review", updateReviewDto.toString()));
        filmRepository.findById(updateReviewDto.filmId())
                .orElseThrow(() -> new EntityNotFoundByIdException("review", updateReviewDto.toString()));
        findByIdOrElseThrow(updateReviewDto.reviewId());

        Review review = Review.builder()
                .useful(reviewRepository.getUseful(updateReviewDto.reviewId()))
                .reviewId(updateReviewDto.reviewId())
                .isPositive(updateReviewDto.isPositive())
                .content(updateReviewDto.content())
                .filmId(updateReviewDto.filmId())
                .userId(updateReviewDto.userId())
                .build();

        reviewRepository.update(updateReviewDto);

        log.info("Update {}", updateReviewDto);
        feedService.addFeed(Feed.builder()
                .entityId(review.getReviewId())
                .userId(review.getUserId())
                .eventType(REVIEW)
                .operation(UPDATE)
                .build());

        return review;
    }

    @Override
    public void remove(final Long id) {
        final long userId = findByIdOrElseThrow(id).getUserId();

        reviewRepository.delete(id);
        feedService.addFeed(Feed.builder()
                .entityId(id)
                .userId(userId)
                .eventType(REVIEW)
                .operation(REMOVE)
                .build());

    }

    public Review findByIdOrElseThrow(final Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new EntityNotFoundByIdException("review", id.toString()));
    }

    @Override
    public Collection<Review> findAll(final Long filmId, final int count) {
        if (ObjectUtils.isEmpty(filmId)) {
            reviewRepository.findMany();
        }

        return reviewRepository.findMany(filmId, count);
    }

    @Override
    public Review addLike(final Long reviewId, final Long userId) {
        Review review = findByIdOrElseThrow(reviewId);
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundByIdException("like", userId.toString()));

        long useful = review.getUseful();
        ++useful;

        saveLike(reviewId, userId, useful, true);
        review.setUseful(useful);

        log.info("addLike {}", review);

        return review;
    }

    @Override
    public Review addDislike(final Long reviewId, final Long userId) {
        Review review = findByIdOrElseThrow(reviewId);
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundByIdException("like", userId.toString()));

        long useful = review.getUseful();
        --useful;

        if (useful == 0) {
            --useful;
        }

        review.setUseful(useful);

        saveLike(reviewId, userId, useful, false);

        log.info("addDislike {}", review);

        return review;
    }

    @Override
    public Review removeLike(final Long reviewId, final Long userId) {
        Review review = findByIdOrElseThrow(reviewId);
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundByIdException("like", userId.toString()));
        long useful = review.getUseful();
        --useful;
        review.setUseful(useful);

        removeLike(reviewId, userId, useful);

        log.info("removeLike {}", review);

        return review;
    }

    @Override
    public Review removeDislike(final Long reviewId, final Long userId) {
        Review review = findByIdOrElseThrow(reviewId);
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundByIdException("like", userId.toString()));
        long useful = review.getUseful();
        ++useful;
        review.setUseful(useful);

        removeLike(reviewId, userId, useful);

        log.info("removeDislike {}", review);

        return review;
    }

    private void removeLike(final Long reviewId, final Long userId, final long useful) {
        reviewRepository.removeLike(reviewId, userId);
        reviewRepository.updateUseful(reviewId, useful);
    }

    private void saveLike(final Long reviewId, final Long userId, final long useful, final boolean isPositive) {
        reviewRepository.saveLike(reviewId, userId, isPositive);
        reviewRepository.updateUseful(reviewId, useful);
    }
}
