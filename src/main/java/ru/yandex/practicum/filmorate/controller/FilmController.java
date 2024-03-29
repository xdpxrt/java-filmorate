package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addMovie(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма {}", film);
        checkMovieInfo(film);
        return filmService.addMovie(film);
    }

    @PutMapping
    public Film updateMovie(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма {}", film);
        checkMovieInfo(film);
        return filmService.updateMovie(film);
    }

    @GetMapping
    public List<Film> getMovies() {
        log.info("Получен запрос на список фильмов");
        return filmService.getMovies();
    }

    @GetMapping("/{id}")
    public Film getMovieById(@PathVariable int id) {
        isAcceptable(id, "id");
        log.info("Получен запрос на получение фильма id {}", id);
        return filmService.getMovieById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(value = "id") int filmId, @PathVariable int userId) {
        isAcceptable(filmId, "filmId");
        isAcceptable(userId, "userId");
        log.info("Получен запрос на добавление лайка");
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable(value = "id") int filmId, @PathVariable int userId) {
        isAcceptable(filmId, "filmId");
        isAcceptable(userId, "userId");
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularMovies(@RequestParam(defaultValue = "10") @Positive int count) {
        log.info("Получен запрос на получение списка популярных фильмов");
        return filmService.getPopularMovies(count);
    }

    private void isAcceptable(int id, String param) {
        if (id < 1) {
            throw new NotFoundException(param);
        }
    }

    private void checkMovieInfo(Film film) throws ValidationException {
        if (film == null) {
            throw new ValidationException("На входе пустой объект!");
        }
    }
}
