package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.film.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.user.dto.UserDto;

public class MappingDto {
    private MappingDto() {
    }

    public static Film mapCreateFilmDtoToFilm(CreateFilmDto createFilmDto) {
        return Film.builder()
                .id(createFilmDto.getId())
                .name(createFilmDto.getName())
                .description(createFilmDto.getDescription())
                .releaseDate(createFilmDto.getReleaseDate())
                .duration(createFilmDto.getDuration())
                .build();
    }

    public static Film mapUpdateFilmDtoToFilm(UpdateFilmDto updateFilmDto) {
        return Film.builder()
                .id(updateFilmDto.getId())
                .name(updateFilmDto.getName())
                .description(updateFilmDto.getDescription())
                .releaseDate(updateFilmDto.getReleaseDate())
                .duration(updateFilmDto.getDuration())
                .build();
    }

    public static User mapCreateUserDtoToUser(CreateUserDto createUserDto) {
        return User.builder()
                .id(createUserDto.getId())
                .email(createUserDto.getEmail())
                .login(createUserDto.getLogin())
                .name(createUserDto.getName())
                .birthday(createUserDto.getBirthday())
                .build();
    }

    public static User mapUpdateUserDtoToUser(UpdateUserDto updateUserDto) {
        return User.builder()
                .id(updateUserDto.getId())
                .email(updateUserDto.getEmail())
                .login(updateUserDto.getLogin())
                .name(updateUserDto.getName())
                .birthday(updateUserDto.getBirthday())
                .build();
    }

    public static FilmDto mapFilmToFilmDto(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();
    }

    public static UserDto mapUserToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
    }

}
