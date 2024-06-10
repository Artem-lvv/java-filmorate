package ru.yandex.practicum.filmorate.model.review.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateReviewDto(
        @NotNull Long reviewId,
        @NotNull String content,
        @NotNull Boolean isPositive,
        @NotNull Long userId,
        @NotNull Long filmId
) {
}
