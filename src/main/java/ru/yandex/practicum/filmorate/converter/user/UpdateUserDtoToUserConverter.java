package ru.yandex.practicum.filmorate.converter.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;

@Component
public class UpdateUserDtoToUserConverter implements Converter<UpdateUserDto, User> {
    @Override
    public User convert(UpdateUserDto src) {
        return User.builder()
                .id(src.id())
                .email(src.email())
                .login(src.login())
                .name(src.name())
                .birthday(src.birthday())
                .build();
    }
}
