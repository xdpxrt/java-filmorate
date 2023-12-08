package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int countId = 1;
    private final Map<Integer, Film> movies = new HashMap<>();

    @Override
    public void addMovie(Film film) {
        film.setId(countId);
        movies.put(countId++, film);
    }

    @Override
    public void updateMovie(Film film) {
        if (movies.containsKey(film.getId())) {
            movies.put(film.getId(), film);
        } else throw new FilmNotFoundException(String.format("Фильма с id%d не существует", film.getId()));
    }

    @Override
    public List<Film> getMovies() {
        return new ArrayList<>(movies.values());
    }

    @Override
    public Film getMovieById(int id) {
        if (!movies.containsKey(id)) {
            return null;
        }
        return movies.get(id);
    }
}
