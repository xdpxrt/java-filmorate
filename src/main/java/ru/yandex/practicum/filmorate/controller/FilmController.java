package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
public class FilmController {
    public static final LocalDate ZERO_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film addMovie(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма " + film);
        checkMovieInfo(film);
        filmStorage.addMovie(film);
        return film;
    }

    @PutMapping("/films")
    public Film updateMovie(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма " + film);
        checkMovieInfo(film);
        filmStorage.updateMovie(film);
        return film;
    }

    @GetMapping("/films")
    public List<Film> getMovies() {
        log.info("Получен запрос на список фильмов");
        return filmStorage.getMovies();
    }

    @GetMapping("/films/{id}")
    public Film getMovieById(@PathVariable int id) {
        isAcceptable(id, "id");
        return filmStorage.getMovieById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable(value = "id") int filmId, @PathVariable int userId) {
        isAcceptable(filmId, "filmId");
        isAcceptable(userId, "userId");
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable(value = "id") int filmId, @PathVariable int userId) {
        isAcceptable(filmId, "filmId");
        isAcceptable(userId, "userId");
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularMovies(@RequestParam(defaultValue = "10", required = false) int count) {
        isAcceptable(count, "count");
        return filmService.getPopularMovies(count);
    }

    private void isAcceptable(int id, String param) {
        if (id < 1) {
            throw new IncorrectParameterException(param);
        }
    }

    private void checkMovieInfo(Film film) throws ValidationException {
        if (film == null) {
            throw new ValidationException("На входе пустой объект!");
        }
        if (film.getReleaseDate().isBefore(ZERO_DATE)) {
            throw new ValidationException("Неверно указана дата выпуска!");
        }
    }
}
