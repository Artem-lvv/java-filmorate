package ru.yandex.practicum.filmorate.model.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    private Long id;
    @EqualsAndHashCode.Include
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
