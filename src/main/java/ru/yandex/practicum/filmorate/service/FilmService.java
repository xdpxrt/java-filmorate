package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int filmId, int userId) {
        if (filmStorage.getMovieById(filmId) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id%d не существует", filmId));
        }
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id%d не существует", userId));
        }
        filmStorage.getMovieById(filmId).getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        if (filmStorage.getMovieById(filmId) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id%d не существует", filmId));
        }
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id%d не существует", userId));
        }
        filmStorage.getMovieById(filmId).getLikes().remove(userId);
    }

    public List<Film> getPopularMovies(int count) {
        return filmStorage.getMovies().stream()
                .sorted(this::compareMoviesByPopular)
                .limit(count)
                .collect(Collectors.toList());
    }

    public void addMovie(Film film) {
        filmStorage.addMovie(film);
    }

    public void updateMovie(Film film) {
        filmStorage.updateMovie(film);
    }

    public List<Film> getMovies() {
        return filmStorage.getMovies();
    }

    public Film getMovieById(int filmId) {
        return filmStorage.getMovieById(filmId);
    }

    private int compareMoviesByPopular(Film f0, Film f1) {
        return f1.getLikes().size() - f0.getLikes().size();
    }
}
