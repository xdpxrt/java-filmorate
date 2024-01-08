package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static int countId = 1;
    private final Map<Integer, Film> movies = new HashMap<>();
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();

    @Override
    public Film addMovie(Film film) {
        film.setId(countId);
        movies.put(countId++, film);
        return film;
    }

    @Override
    public Film updateMovie(Film film) {
        int filmId = film.getId();
        if (filmId < 1) {
            throw new ValidationException("Неверно указан id");
        }
        movieExist(filmId);
        movies.put(filmId, film);
        return film;
    }

    @Override
    public List<Film> getMovies() {
        return new ArrayList<>(movies.values());
    }

    @Override
    public Film getMovieById(int filmId) {
        movieExist(filmId);
        return movies.get(filmId);
    }

    @Override
    public void addLike(int filmId, int userId) {
        movieExist(filmId);
        likes.get(filmId).add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        movieExist(filmId);
        likes.get(filmId).remove(userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return movies.values().stream()
                .sorted(this::compareMoviesByPopular)
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void movieExist(int id) {
        if (movies.get(id) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id%d не существует", id));
        }
    }

    private int compareMoviesByPopular(Film f0, Film f1) {
        return likes.get(f1.getId()).size() - likes.get(f0.getId()).size();
    }
}
