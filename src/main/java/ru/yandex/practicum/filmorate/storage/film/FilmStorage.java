package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    void addMovie(Film film);

    void updateMovie(Film film);

    List<Film> getMovies();

    Film getMovieById(int id);
}
