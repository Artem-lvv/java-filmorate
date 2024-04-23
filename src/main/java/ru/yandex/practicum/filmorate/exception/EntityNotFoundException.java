package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityNotFoundException extends ResponseStatusException {
    public EntityNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, "No entity with id: [%s] found".formatted(id));
    }
}
