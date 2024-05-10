package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityNotFoundByIdException extends ResponseStatusException {
    public EntityNotFoundByIdException(String nameEntity, String id) {
        super(HttpStatus.NOT_FOUND, "No entity [%s] with id: [%s]".formatted(nameEntity, id));
    }
}
