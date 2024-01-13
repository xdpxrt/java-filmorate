package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;
    Film film;

    @BeforeEach
    public void init() {
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
        film = new Film("name", "description",
                LocalDate.of(1991, 10, 14), 148);
        film.setId(1);
        film.setMpa(new MPA(1, "G"));
    }

    @Test
    public void addMovieTest() {
        assertEquals(film, filmDbStorage.addMovie(film));
    }

    @Test
    public void getMovieByIdTest() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(6, "Боевик"));
        film.setGenres(genres);
        filmDbStorage.addMovie(film);
        assertEquals(film, filmDbStorage.getMovieById(1));
    }

    @Test
    public void getMovieWithWrongIdTest() {
        assertThrows(NotFoundException.class, () -> filmDbStorage.getMovieById(0), "Фильм с id0 не найден");
    }

    @Test
    public void updateMovieTest() {
        filmDbStorage.addMovie(film);
        Film newFilm = new Film("newName", "newDescription",
                LocalDate.of(1990, 9, 10), 120);
        newFilm.setMpa(new MPA(2, "PG"));
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(6, "Боевик"));
        newFilm.setGenres(genres);
        newFilm.setId(1);
        assertEquals(newFilm, filmDbStorage.updateMovie(newFilm));
    }

    @Test
    public void updateMovieWithWrongIdTest() {
        filmDbStorage.addMovie(film);
        Film newFilm = new Film("newName", "newDescription",
                LocalDate.of(1990, 9, 10), 120);
        newFilm.setMpa(new MPA(2, "PG"));
        newFilm.setId(2);
        assertThrows(NotFoundException.class, () ->
                filmDbStorage.updateMovie(newFilm), "Фильм с id2 не найден");
    }

    @Test
    public void getMoviesTest() {
        filmDbStorage.addMovie(film);
        Film newFilm = new Film("newName", "newDescription",
                LocalDate.of(1990, 9, 10), 120);
        newFilm.setMpa(new MPA(2, "PG"));
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(6, "Боевик"));
        newFilm.setGenres(genres);
        newFilm.setId(2);
        filmDbStorage.addMovie(newFilm);
        List<Film> films = filmDbStorage.getMovies();
        assertEquals(2, films.size());
        assertEquals(film, films.get(0));
        assertEquals(newFilm, films.get(1));
    }

    @Test
    public void getMoviesEmptyTest() {
        assertEquals(0, filmDbStorage.getMovies().size());
    }

    @Test
    public void getPopularMoviesEmptyTest() {
        assertEquals(0, filmDbStorage.getPopularFilms(5).size());
    }

    @Test
    public void getPopularFilmsWithoutLikesTest() {
        filmDbStorage.addMovie(film);
        assertEquals(1, filmDbStorage.getPopularFilms(5).size());
    }

    @Test
    public void addLikeTest() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        filmDbStorage.addMovie(film);
        userDbStorage.addUser(new User("sergio@ya.ru", "sergio90", "Sergio",
                LocalDate.of(1990, 4, 15)));
        filmDbStorage.addLike(1, 1);
        List<Film> popularFilms = filmDbStorage.getPopularFilms(5);
        assertEquals(1, popularFilms.size());
        assertEquals(film, popularFilms.get(0));
    }

    @Test
    public void removeLikeTest() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        filmDbStorage.addMovie(film);
        Film newFilm = new Film("newName", "newDescription",
                LocalDate.of(1990, 9, 10), 120);
        newFilm.setMpa(new MPA(2, "PG"));
        newFilm.setId(2);
        filmDbStorage.addMovie(newFilm);
        userDbStorage.addUser(new User("sergio@ya.ru", "sergio90", "Sergio",
                LocalDate.of(1990, 4, 15)));
        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(2, 1);
        filmDbStorage.removeLike(1, 1);
        List<Film> popularFilms = filmDbStorage.getPopularFilms(5);
        assertEquals(1, popularFilms.size());
        assertEquals(newFilm, popularFilms.get(0));
    }
}
