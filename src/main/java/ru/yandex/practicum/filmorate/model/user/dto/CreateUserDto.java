package ru.yandex.practicum.filmorate.model.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.yandex.practicum.filmorate.annotation.DoesNotContainSubstring;

import java.io.Serializable;
import java.time.LocalDate;

@Value
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"email"})
public class CreateUserDto implements Serializable {
    private Long id;
    @NotBlank(message = "Value cannot be empty or null")
    @Email(message = "Value must contains character @")
    private String email;
    @NotBlank(message = "Value cannot be empty or null")
    @DoesNotContainSubstring(value = " ", message = "Value must not contain spaces")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
