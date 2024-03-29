package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable int id) {
        log.info("Получен запрос на получение жанра по id:{}", id);
        if (id < 1) {
            throw new IncorrectParameterException("id");
        }
        return genreService.getGenre(id);
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("Получен запрос на получение списка жанров");
        return genreService.getAllGenres();
    }
}
