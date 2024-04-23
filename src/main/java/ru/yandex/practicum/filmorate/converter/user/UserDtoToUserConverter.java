package ru.yandex.practicum.filmorate.converter.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;

@Component
public class UserDtoToUserConverter implements Converter<User, UserDto> {
    @Override
    public UserDto convert(User src) {
        return UserDto.builder()
                .id(src.getId())
                .email(src.getEmail())
                .login(src.getLogin())
                .name(src.getName())
                .birthday(src.getBirthday())
                .build();
    }
}
