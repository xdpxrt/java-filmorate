package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        if (userIsExist(userId) && userIsExist(friendId)) {
            userStorage.getUserById(userId).getFriends().add(friendId);
            userStorage.getUserById(friendId).getFriends().add(userId);
        }
    }

    public void removeFriend(int userId, int friendId) {
        if (userIsExist(userId) && userIsExist(friendId)) {
            userStorage.getUserById(userId).getFriends().remove(friendId);
            userStorage.getUserById(friendId).getFriends().remove(userId);
        }
    }

    public List<User> getFriends(int userId) {
        if (userIsExist(userId)) {
            List<Integer> friends = new ArrayList<>(userStorage.getUserById(userId).getFriends());
            return userStorage.getUsers().stream()
                    .filter(user -> friends.contains(user.getId()))
                    .collect(Collectors.toList());
        } else return new ArrayList<>();
    }

    public List<User> getMutualFriends(int user1Id, int user2Id) {
        if (userIsExist(user1Id) && userIsExist(user2Id)) {
            List<Integer> user1Friends = new ArrayList<>(userStorage.getUserById(user1Id).getFriends());
            List<Integer> user2Friends = new ArrayList<>(userStorage.getUserById(user2Id).getFriends());
            return user1Friends.stream()
                    .filter(user2Friends::contains)
                    .map(userStorage::getUserById)
                    .collect(Collectors.toList());
        } else return new ArrayList<>();
    }

    private boolean userIsExist(int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id%d не существует", userId));
        } else return true;
    }
}
