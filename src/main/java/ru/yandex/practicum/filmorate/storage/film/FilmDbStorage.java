package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addMovie(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("films")
                .usingGeneratedKeyColumns("id");
        int filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.setId(filmId);
        String sqlMPA = "INSERT INTO films_mpa (film_id, mpa_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlMPA, filmId, film.getMpa().getId());
        insertGenres(film);
        return film;
    }

    @Override
    public Film updateMovie(Film film) {
        String sqlFilmUpdate =
                "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ? " + "where id = ?";
        jdbcTemplate.update(sqlFilmUpdate, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getId());
        String sqlMPAUpdate = "UPDATE films_mpa SET mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlMPAUpdate, film.getMpa().getId(), film.getId());
        String sqlDeleteGenres = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteGenres, film.getId());
        insertGenres(film);
        return getMovieById(film.getId());
    }

    @Override
    public List<Film> getMovies() {
        String sqlMovies = "SELECT f.*, m.id AS mpaId, m.name AS mpaName " +
                "FROM films AS f " +
                "JOIN films_mpa AS fm ON f.id = fm.film_id " +
                "JOIN mpa AS m ON fm.mpa_id = m.id";
        List<Film> movies = jdbcTemplate.query(sqlMovies, this::filmRow);
        Map<Integer, List<Genre>> filmsGenres = new HashMap<>();
        String sqlGenres = "SELECT fg.film_id AS filmId, g.id AS genreId, g.name AS genreName " +
                "FROM films_genres AS fg " +
                "JOIN genres AS g ON fg.genre_id = g.id";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlGenres);
        while (sqlRowSet.next()) {
            int filmId = sqlRowSet.getInt("FILMID");
            int genreId = sqlRowSet.getInt("GENREID");
            String genreName = sqlRowSet.getString("GENRENAME");
            Genre genre = new Genre(genreId, genreName);
            if (!filmsGenres.containsKey(filmId)) {
                filmsGenres.put(filmId, new ArrayList<>());
            }
            filmsGenres.get(filmId).add(genre);
        }
        for (Film film : movies) {
            if (filmsGenres.containsKey(film.getId())) {
                film.setGenres(filmsGenres.get(film.getId()));
            }
        }
        return movies;
    }

    @Override
    public Film getMovieById(int id) {
        String sql = "SELECT f.*, m.id AS mpaId, m.name AS mpaName " +
                "FROM films AS f " +
                "JOIN films_mpa AS fm ON f.id = fm.film_id " +
                "JOIN mpa AS m ON fm.mpa_id = m.id " +
                "WHERE f.id = ?";
        String sqlGenres = "SELECT g.id AS genreId, g.name AS genreName " +
                "FROM films_genres AS fg " +
                "JOIN genres AS g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?";
        Film film = jdbcTemplate.query(sql, this::filmRow, id).stream().findAny().orElseThrow(() ->
                new FilmNotFoundException(String.format("Фильм с id%d не найден", id)));
        List<Genre> genres = jdbcTemplate.query(sqlGenres, this::genreRow, id);
        if (film != null) {
            film.setGenres(genres);
        }
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update("INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM films_likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.id AS mpaId, m.name AS mpaName " +
                "FROM films AS f " +
                "JOIN films_likes AS fl ON f.ID = fl.film_id " +
                "JOIN films_mpa AS fm ON f.id = fm.film_id " +
                "JOIN mpa AS m ON fm.mpa_id = m.id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT (fl.user_id) DESC LIMIT ?";
        List<Film> popularFilms = jdbcTemplate.query(sql, this::filmRow, count);
        if (popularFilms.isEmpty()) {
            return getMovies();
        }
        return popularFilms;
    }

    public void movieExist(int id) {
        jdbcTemplate.query("SELECT f.* FROM films AS f WHERE f.id = ?", this::rawFilmRow, id)
                .stream().findAny().orElseThrow(() ->
                        new FilmNotFoundException(String.format("Фильм с id%d не найден", id)));
    }

    private Film filmRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(rs.getString("title"), rs.getString("description"),
                rs.getDate("release_date").toLocalDate(), rs.getInt("duration"));
        film.setId((rs.getInt("id")));
        film.setMpa(new MPA(rs.getInt("mpaId"), rs.getString("mpaName")));
        return film;
    }

    private Film rawFilmRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(rs.getString("title"), rs.getString("description"),
                rs.getDate("release_date").toLocalDate(), rs.getInt("duration"));
        film.setId((rs.getInt("id")));
        return film;
    }

    private Genre genreRow(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }

    private void insertGenres(Film film) {
        String sqlGenre = "MERGE INTO films_genres (film_id, genre_id) VALUES (?, ?)";
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlGenre, film.getId(), genre.getId());
            }
        }
    }
}

