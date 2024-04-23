package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import ru.yandex.practicum.filmorate.annotation.validator.DoesNotContainSubstringValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DoesNotContainSubstringValidator.class)
public @interface DoesNotContainSubstring {
    String message() default "Value must not contain substring {value}";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
    String value();
}
