package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularMovies(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film addMovie(Film film) {
        return filmStorage.addMovie(film);
    }

    public Film updateMovie(Film film) {
        filmStorage.movieExist(film.getId());
        return filmStorage.updateMovie(film);
    }

    public List<Film> getMovies() {
        return filmStorage.getMovies();
    }

    public Film getMovieById(int filmId) {
        filmStorage.movieExist(filmId);
        return filmStorage.getMovieById(filmId);
    }

}
