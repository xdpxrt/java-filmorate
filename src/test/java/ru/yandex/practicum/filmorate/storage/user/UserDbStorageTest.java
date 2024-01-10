package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userDbStorage;
    private User user;

    @BeforeEach
    public void init() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
        user = new User("sergio@ya.ru", "sergio90", "Sergio",
                LocalDate.of(1990, 4, 15));
    }

    @Test
    public void addUserTest() {
        assertEquals(user, userDbStorage.addUser(user));
    }

    @Test
    public void updateUserTest() {
        userDbStorage.addUser(user);
        User newUser = new User("newsergio@ya.ru", "newsergio90", "newSergio",
                LocalDate.of(1990, 4, 10));
        newUser.setId(1);
        assertEquals(newUser, userDbStorage.updateUser(newUser));
    }

    @Test
    public void updateUserWrongIdTest() {
        userDbStorage.addUser(user);
        user.setId(2);
        assertThrows(UserNotFoundException.class,
                () -> userDbStorage.updateUser(user), "Пользователь с id2 не найден");
    }

    @Test
    public void getUsersTest() {
        userDbStorage.addUser(user);
        assertEquals(1, userDbStorage.getUsers().size());
        assertEquals(user, userDbStorage.getUsers().get(0));
    }

    @Test
    public void getUsersEmptyTest() {
        assertEquals(0, userDbStorage.getUsers().size());
    }

    @Test
    public void getUserByIdTest() {
        userDbStorage.addUser(user);
        assertEquals(user, userDbStorage.getUserById(1));
    }

    @Test
    public void getUserByIdWrongIdTest() {
        assertThrows(UserNotFoundException.class,
                () -> userDbStorage.getUserById(1), "Пользователь с id1 не найден");
    }

    @Test
    public void addFriendTest() {
        userDbStorage.addUser(user);
        User newUser = new User("newsergio@ya.ru", "newsergio90", "newSergio",
                LocalDate.of(1990, 4, 10));
        newUser.setId(2);
        userDbStorage.addUser(newUser);
        userDbStorage.addFriend(1, 2);
        assertEquals(1, userDbStorage.getFriends(1).size());
        assertEquals(newUser, userDbStorage.getFriends(1).get(0));
    }

    @Test
    public void addFriendWrongUserIdTest() {
        assertThrows(UserNotFoundException.class,
                () -> userDbStorage.addFriend(1, 2), "Пользователь с id1 не найден");
    }

    @Test
    public void addFriendWrongFriendIdTest() {
        userDbStorage.addUser(user);
        assertThrows(UserNotFoundException.class,
                () -> userDbStorage.addFriend(1, 2), "Пользователь с id2 не найден");
    }

    @Test
    public void removeFriendTest() {
        userDbStorage.addUser(user);
        User newUser = new User("newsergio@ya.ru", "newsergio90", "newSergio",
                LocalDate.of(1990, 4, 10));
        newUser.setId(2);
        userDbStorage.addUser(newUser);
        userDbStorage.addFriend(1, 2);
        userDbStorage.removeFriend(1, 2);
        assertEquals(0, userDbStorage.getFriends(1).size());
    }

    @Test
    public void removeFriendWrongUserIdTest() {
        assertThrows(UserNotFoundException.class,
                () -> userDbStorage.removeFriend(1, 2), "Пользователь с id1 не найден");
    }

    @Test
    public void removeFriendWrongFriendIdTest() {
        userDbStorage.addUser(user);
        assertThrows(UserNotFoundException.class,
                () -> userDbStorage.removeFriend(1, 2), "Пользователь с id2 не найден");
    }

    @Test
    public void getMutualFriendsTest() {
        userDbStorage.addUser(user);
        User newUser = new User("newsergio@ya.ru", "newsergio90", "newSergio",
                LocalDate.of(1990, 4, 10));
        newUser.setId(2);
        userDbStorage.addUser(newUser);
        User anotherUser = new User("new@ya.ru", "new", "new",
                LocalDate.of(1980, 5, 1));
        newUser.setId(3);
        userDbStorage.addUser(anotherUser);
        userDbStorage.addFriend(1, 3);
        userDbStorage.addFriend(2, 3);
        List<User> mutualFriends = userDbStorage.getMutualFriends(1, 2);
        assertEquals(1, mutualFriends.size());
        assertEquals(anotherUser, mutualFriends.get(0));
    }
}
