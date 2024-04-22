package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityDuplicateException extends ResponseStatusException {
    public EntityDuplicateException(String nameField, String value) {
        super(HttpStatus.CONFLICT, "Duplicate entity by field: [%s] [%s]".formatted(nameField, value));
    }
}
