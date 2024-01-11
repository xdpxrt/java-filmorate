package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Primary
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("users")
                .usingGeneratedKeyColumns("id");
        int userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        user.setId(userId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET login = ?, name = ?, email = ?, birthday = ? where id = ?";
        jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        return getUserById(user.getId());
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM users", this::userRow);
    }

    @Override
    public User getUserById(int id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", this::userRow, id)
                .stream().findAny().orElseThrow(() ->
                        new NotFoundException(String.format("Пользователь с id%d не найден", id)));
    }

    @Override
    public void addFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        jdbcTemplate.update("MERGE INTO friends (user_id, friend_id) VALUES (?, ?)", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        getUserById(userId);
        String sql = "SELECT u.* FROM friends AS f JOIN users AS u ON f.friend_id = u.id WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, this::userRow, userId);
    }

    @Override
    public List<User> getMutualFriends(int user1Id, int user2Id) {
        getUserById(user1Id);
        getUserById(user2Id);
        String sql = "SELECT u.* FROM users AS u " +
                "WHERE u.id IN (" +
                "SELECT f.friend_id FROM friends AS f " +
                "WHERE f.user_id = ? AND f.friend_id IN (" +
                "SELECT f.friend_id FROM friends AS f " +
                "WHERE f.user_id = ?))";
        return jdbcTemplate.query(sql, this::userRow, user1Id, user2Id);
    }

    private User userRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User(rs.getString("email"), rs.getString("login"),
                rs.getString("name"), rs.getDate("birthday").toLocalDate());
        user.setId(rs.getInt("id"));
        return user;
    }
}
