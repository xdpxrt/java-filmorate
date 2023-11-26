package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    public static final LocalDate ZERO_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> movies = new HashMap<>();
    private int countId = 1;

    @PostMapping
    public Film addMovie(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма " + film);
        if (checkMovieInfo(film)) {
            film.setId(countId);
            movies.put(countId++, film);
        }
        return film;
    }

    @PutMapping
    public Film updateMovie(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма " + film);
        int filmId;
        if (checkMovieInfo(film)) {
            filmId = film.getId();
            if (movies.containsKey(filmId)) {
                movies.put(filmId, film);
            } else throw new ValidationException("Фильма с id:" + filmId + " не существует");
        }
        return film;
    }

    @GetMapping
    public List<Film> getMovies() {
        log.info("Получен запрос на список фильмов");
        return new ArrayList<>(movies.values());
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
