package ru.yandex.practicum.filmorate.converter.review;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.model.review.dto.UpdateReviewDto;

@Component
public class UpdateReviewDtoToReviewConverter implements Converter<UpdateReviewDto, Review> {
    @Override
    public Review convert(UpdateReviewDto src) {
        return Review.builder()
                .reviewId(src.reviewId())
                .content(src.content())
                .isPositive(src.isPositive())
                .userId(src.userId())
                .filmId(src.filmId())
                .build();
    }
}
