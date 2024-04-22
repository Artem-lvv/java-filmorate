package ru.yandex.practicum.filmorate.model.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
