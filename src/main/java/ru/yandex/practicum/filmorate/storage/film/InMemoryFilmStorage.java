package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int countId = 1;
    private final Map<Integer, Film> movies = new HashMap<>();

    @Override
    public void addMovie(Film film) {
        film.setId(countId);
        film.setLikes(new HashSet<>());
        movies.put(countId++, film);
    }

    @Override
    public void updateMovie(Film film) {
        int filmId = film.getId();
        if (filmId < 1) {
            throw new ValidationException("Неверно указан id");
        }
        if (!movies.containsKey(filmId)) {
            throw new FilmNotFoundException(String.format("Фильма с id%d не существует", filmId));
        }
        Set<Integer> likes = movies.get(filmId).getLikes();
        film.setLikes(likes);
        movies.put(filmId, film);
    }

    @Override
    public List<Film> getMovies() {
        return new ArrayList<>(movies.values());
    }

    @Override
    public Film getMovieById(int filmId) {
        if (!movies.containsKey(filmId)) {
            throw new FilmNotFoundException(String.format("Фильма с id%d не существует", filmId));
        }
        return movies.get(filmId);
    }
}
