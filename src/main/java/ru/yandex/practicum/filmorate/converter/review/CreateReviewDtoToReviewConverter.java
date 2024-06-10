package ru.yandex.practicum.filmorate.converter.review;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.model.review.dto.CreateReviewDto;

@Component
public class CreateReviewDtoToReviewConverter implements Converter<CreateReviewDto, Review> {
    @Override
    public Review convert(CreateReviewDto src) {
        return Review.builder()
                .content(src.content())
                .isPositive(src.isPositive())
                .userId(src.userId())
                .filmId(src.filmId())
                .build();
    }
}
