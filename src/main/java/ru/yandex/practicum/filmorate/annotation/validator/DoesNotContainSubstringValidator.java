package ru.yandex.practicum.filmorate.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.DoesNotContainSubstring;

public class DoesNotContainSubstringValidator implements ConstraintValidator<DoesNotContainSubstring, String> {
    private String subString;

    @Override
    public void initialize(DoesNotContainSubstring constraintAnnotation) {
        subString = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !s.contains(subString);
    }
}
