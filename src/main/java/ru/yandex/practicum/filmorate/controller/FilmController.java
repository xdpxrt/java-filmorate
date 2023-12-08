package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    public static final LocalDate ZERO_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @PostMapping
    public Film addMovie(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма " + film);
        if (checkMovieInfo(film)) {
            filmStorage.addMovie(film);
        }
        return film;
    }

    @PutMapping
    public Film updateMovie(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма " + film);
        if (checkMovieInfo(film)) {
            filmStorage.updateMovie(film);
        }
        return film;
    }

    @GetMapping
    public List<Film> getMovies() {
        log.info("Получен запрос на список фильмов");
        return filmStorage.getMovies();
    }

    private boolean checkMovieInfo(Film film) throws ValidationException {
        if (film == null) {
            throw new ValidationException("На входе пустой объект!");
        }
        if (film.getReleaseDate().isBefore(ZERO_DATE)) {
            throw new ValidationException("Неверно указана дата выпуска!");
        }
        return true;
    }
}
