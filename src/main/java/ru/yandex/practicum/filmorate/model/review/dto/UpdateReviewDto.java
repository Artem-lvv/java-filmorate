package ru.yandex.practicum.filmorate.model.review.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateReviewDto(
        @NotNull Long reviewId,
        @NotNull String content,
        @NotNull Boolean isPositive
) {
}
