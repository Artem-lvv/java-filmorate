package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.dto.GenreDto;
import ru.yandex.practicum.filmorate.storage.inDataBase.GenreDBStorage;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreDBStorage genreDBStorage;

    @GetMapping
    public List<GenreDto> findAll() {
        return genreDBStorage.findAll();
    }

    @GetMapping("/{id}")
    public GenreDto findById(@PathVariable Long id) {
        return genreDBStorage.findById(id);
    }

}
