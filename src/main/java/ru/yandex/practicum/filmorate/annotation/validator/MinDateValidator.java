package ru.yandex.practicum.filmorate.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.MinDate;

import java.time.LocalDate;
import java.util.Objects;

public class MinDateValidator implements ConstraintValidator<MinDate, LocalDate> {
    private LocalDate minimumDate;

    @Override
    public void initialize(MinDate constraintAnnotation) {
        minimumDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.nonNull(localDate) && localDate.isAfter(minimumDate);
    }
}
