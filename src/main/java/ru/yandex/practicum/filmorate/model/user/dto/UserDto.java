package ru.yandex.practicum.filmorate.model.user.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserDto(
        Long id,
        String email,
        String login,
        String name,
        LocalDate birthday
) {
}
