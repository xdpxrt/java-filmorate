package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private Set<ConstraintViolation<Film>> violations;
    private FilmController filmController;
    private Film film;

    @BeforeEach
    public void init() {
        filmController = new FilmController();
    }

    @Test
    public void filmCreateTest() {
        film = new Film("movieName", "movieDescription",
                LocalDate.of(1991, 10, 10), 100);
        filmController.addMovie(film);
        violations = validator.validate(film);
        assertEquals(film, filmController.getMovies().get(0));
        assertEquals(0, violations.size());
    }

    @Test
    public void filmCreateBadNameTest() {
        film = new Film("", "movieDescription",
                LocalDate.of(1991, 10, 10), 100);
        film.setId(1);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void filmCreateBadDescriptionTest() {
        film = new Film("movieName", "movieDescriptionmovieDescriptionmovieDescriptionmovieDescripti" +
                "onmovieDescriptionmovieDescriptionmovieDescriptionmovieDescriptionmovieDescriptionmovieDescriptionm" +
                "ovieDescriptionmovieDescriptionmovieDescriptionmovieDescriptionmovieDescriptionmovieDescriptionmovi" +
                "eDescription",
                LocalDate.of(1991, 10, 10), 100);
        film.setId(1);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void filmCreateBadReleaseDateTest() {
        film = new Film("movieName", "movieDescription",
                LocalDate.of(1786, 10, 10), 100);
        assertThrows(ValidationException.class,
                () -> filmController.addMovie(film),
                "Неверно указана дата выпуска!");
    }

    @Test
    public void filmCreateBadDurationTest() {
        film = new Film("movieName", "movieDescription",
                LocalDate.of(1991, 10, 10), -100);
        film.setId(1);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void filmUpdateTest() {
        film = new Film("movieName", "movieDescription",
                LocalDate.of(1991, 10, 10), 100);
        filmController.addMovie(film);
        Film updatedFilm = new Film("newName", "newDescription",
                LocalDate.of(1990, 11, 11), 120);
        updatedFilm.setId(1);
        filmController.updateMovie(updatedFilm);
        assertEquals(updatedFilm, filmController.getMovies().get(0));
    }

    @Test
    public void filmUpdateUnknownTest() {
        Film film = new Film("newName", "newDescription",
                LocalDate.of(1990, 11, 11), 120);
        film.setId(1);
        assertThrows(ValidationException.class,
                () -> filmController.updateMovie(film),
                "Фильма с id:1 не существует");
    }

    @Test
    public void filmGetAllTest() {
        film = new Film("movieName", "movieDescription",
                LocalDate.of(1991, 10, 10), 100);
        filmController.addMovie(film);
        filmController.addMovie(new Film("newName", "newDescription",
                LocalDate.of(1990, 11, 11), 120));
        assertEquals(2, filmController.getMovies().size());
    }

    @Test
    public void addEmptyFilmTest() {
        assertThrows(ValidationException.class,
                () -> filmController.addMovie(film),
                "На входе пустой объект!");
    }

    @Test
    public void updateEmptyFilmTest() {
        assertThrows(ValidationException.class,
                () -> filmController.updateMovie(film),
                "На входе пустой объект!");
    }
}