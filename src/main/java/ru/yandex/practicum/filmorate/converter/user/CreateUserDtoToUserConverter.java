package ru.yandex.practicum.filmorate.converter.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;

import java.util.HashSet;

@Component
public class CreateUserDtoToUserConverter implements Converter<CreateUserDto, User> {
    @Override
    public User convert(CreateUserDto src) {
        return User.builder()
                .id(src.id())
                .email(src.email())
                .login(src.login())
                .name(src.name())
                .birthday(src.birthday())
                .build();
    }
}
