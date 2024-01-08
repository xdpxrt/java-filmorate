package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MPADao;
import ru.yandex.practicum.filmorate.exception.MPANotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class MPAStorage implements MPADao {
    private final JdbcTemplate jdbcTemplate;

    public MPAStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MPA getMPA(int mpaId) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        return jdbcTemplate.query(sql, this::mapRow, mpaId).stream().findAny().orElseThrow(() ->
                new MPANotFoundException(String.format("Рейтинга с id%d не существует", mpaId)));

    }

    public List<MPA> getAllMPA() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new MPA(rs.getInt("id"), rs.getString("name"));
    }
}
