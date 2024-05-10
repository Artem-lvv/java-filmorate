package ru.yandex.practicum.filmorate.model.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import ru.yandex.practicum.filmorate.annotation.DoesNotContainSubstring;

import java.time.LocalDate;

public record CreateUserDto(
        Long id,
        @NotBlank(message = "Value cannot be empty or null")
        @Email(message = "Value must contains character @")
        String email,
        @DoesNotContainSubstring(value = " ", message = "Value must not contain spaces")
        @NotBlank(message = "Value cannot be empty or null")
        String login,
        String name,
        @Past
        LocalDate birthday) {
}
