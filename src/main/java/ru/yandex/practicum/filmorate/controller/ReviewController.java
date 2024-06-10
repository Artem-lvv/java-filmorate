package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.model.review.dto.CreateReviewDto;
import ru.yandex.practicum.filmorate.model.review.dto.UpdateReviewDto;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewStorage reviewStorage;

    @GetMapping("/{id}")
    public Review findOne(@PathVariable final Long id) {
        return reviewStorage.findByIdOrElseThrow(id);
    }

    @GetMapping
    public Collection<Review> findAll(@RequestParam(required = false) final Long filmId,
                                      @RequestParam(defaultValue = "10") final int count) {
        return reviewStorage.findAll(filmId, count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@Valid @RequestBody final CreateReviewDto createReviewDto) {
        return reviewStorage.create(createReviewDto);
    }

    @PutMapping
    public Review update(@Valid @RequestBody final UpdateReviewDto updateReviewDto) {
        return reviewStorage.update(updateReviewDto);
    }

    @DeleteMapping(value = "/{id}")
    public void removeLike(@PathVariable final Long id) {
        reviewStorage.remove(id);
    }

    @PutMapping(value = "{reviewId}/like/{userId}")
    public Review updateLike(@PathVariable final Long reviewId, @PathVariable final Long userId) {
        return reviewStorage.addLike(reviewId, userId);
    }

    @PutMapping(value = "{reviewId}/dislike/{userId}")
    public Review updateDislike(@PathVariable final Long reviewId, @PathVariable final Long userId) {
        return reviewStorage.addDislike(reviewId, userId);
    }

    @DeleteMapping(value = "{reviewId}/like/{userId}")
    public Review removeLike(@PathVariable final Long reviewId, @PathVariable final Long userId) {
        return reviewStorage.removeLike(reviewId, userId);
    }

    @DeleteMapping(value = "{reviewId}/dislike/{userId}")
    public Review removeDislike(@PathVariable final Long reviewId, @PathVariable final Long userId) {
        return reviewStorage.removeDislike(reviewId, userId);
    }


}
