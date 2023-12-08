package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public void addMovie(Film film);

    public void updateMovie(Film film);

    public List<Film> getMovies();

    public Film getMovieById(int id);
}
