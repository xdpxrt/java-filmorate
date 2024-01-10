package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void getGenreByIdTest() {
        GenreStorage genreStorage = new GenreStorage(jdbcTemplate);
        Genre genre = genreStorage.getGenre(1);
        assertEquals(1, genre.getId());
    }

    @Test
    public void getGenreByIdWithWrongIdTest() {
        GenreStorage genreStorage = new GenreStorage(jdbcTemplate);
        assertThrows(GenreNotFoundException.class,
                () -> genreStorage.getGenre(0), "Жанра с id0 не существует");
    }

    @Test
    public void getAllGenresTest() {
        GenreStorage genreStorage = new GenreStorage(jdbcTemplate);
        Genre genre1 = genreStorage.getGenre(1);
        Genre genre3 = genreStorage.getGenre(3);
        Genre genre6 = genreStorage.getGenre(6);
        List<Genre> genresFromStorage = genreStorage.getAllGenres();
        assertEquals(genre1, genresFromStorage.get(0));
        assertEquals(genre3, genresFromStorage.get(2));
        assertEquals(genre6, genresFromStorage.get(5));
    }
}
