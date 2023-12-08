package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
public class FilmServiceController {
    private final FilmService filmService;

    @Autowired
    public FilmServiceController(FilmService filmService) {
        this.filmService = filmService;
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

    @GetMapping("/films/popular?count={count}")
    public List<Film> getPopularMovies(@RequestParam(defaultValue = "10", required = false) int count) {
        isAcceptable(count, "count");
        return filmService.getPopularMovies(count);
    }

    private boolean isAcceptable(int id, String param) {
        if (id <= 0) {
            throw new IncorrectParameterException(param);
        }
        return true;
    }
}
