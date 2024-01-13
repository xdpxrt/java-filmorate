package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        insertGenres(film);
        return film;
    }

    @Override
    public Film updateMovie(Film film) {
        String sqlFilmUpdate =
                "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sqlFilmUpdate, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        String sqlDeleteGenres = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteGenres, film.getId());
        insertGenres(film);
        return getMovieById(film.getId());
    }

    @Override
    public List<Film> getMovies() {
        String sqlMovies = "SELECT f.*, m.id AS mpaId, m.name AS mpaName " +
                "FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.id";
        List<Film> movies = jdbcTemplate.query(sqlMovies, this::filmRow);
        return getMoviesWithGenres(movies);
    }

    @Override
    public Film getMovieById(int id) {
        String sql = "SELECT f.*, m.id AS mpaId, m.name AS mpaName " +
                "FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";
        Film film = jdbcTemplate.query(sql, this::filmRow, id).stream().findAny().orElseThrow(() ->
                new NotFoundException(String.format("Фильм с id%d не найден", id)));
        return getMoviesWithGenres(List.of(film)).get(0);
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
                "JOIN mpa AS m ON f.mpa_id = m.id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT (fl.user_id) DESC LIMIT ?";
        List<Film> popularFilms = jdbcTemplate.query(sql, this::filmRow, count);
        if (popularFilms.isEmpty()) {
            return getMovies();
        }
        return getMoviesWithGenres(popularFilms);
    }

    public void movieExist(int id) {
        jdbcTemplate.query("SELECT f.* FROM films AS f WHERE f.id = ?", this::rawFilmRow, id)
                .stream().findAny().orElseThrow(() ->
                        new NotFoundException(String.format("Фильм с id%d не найден", id)));
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

    private List<Film> getMoviesWithGenres(List<Film> movies) {
        final Map<Integer, Film> moviesMap = movies.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        String inSql = String.join(",", Collections.nCopies(moviesMap.size(), "?"));
        String sqlGenres = String.format("SELECT fg.film_id AS filmId, g.id AS genreId, g.name AS genreName " +
                "FROM films_genres AS fg " +
                "JOIN genres AS g ON fg.genre_id = g.id " +
                "WHERE fg.film_id IN (%s)", inSql);
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlGenres, moviesMap.keySet().toArray());
        while (sqlRowSet.next()) {
            int filmId = sqlRowSet.getInt("FILMID");
            int genreId = sqlRowSet.getInt("GENREID");
            String genreName = sqlRowSet.getString("GENRENAME");
            Genre genre = new Genre(genreId, genreName);
            moviesMap.get(filmId).getGenres().add(genre);
        }
        return new ArrayList<>(moviesMap.values());
    }

    private void insertGenres(Film film) {
        List<Genre> genres = new ArrayList<>(film.getGenres());
        String sqlGenre = "MERGE INTO films_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sqlGenre, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, film.getId());
                ps.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }
}

