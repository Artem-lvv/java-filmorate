package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.dto.MPADto;
import ru.yandex.practicum.filmorate.storage.inDataBase.MPADBStorage;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MotionPictureAssociationController {
    private final MPADBStorage mpadbStorage;

    @GetMapping
    public List<MPADto> findAll() {
        return mpadbStorage.findAll();
    }

    @GetMapping("/{id}")
    public MPADto findById(@PathVariable Long id) {
        return mpadbStorage.findById(id);
    }

}
