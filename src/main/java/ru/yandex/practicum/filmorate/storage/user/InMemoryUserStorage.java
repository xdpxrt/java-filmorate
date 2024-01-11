package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int countId = 1;
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(countId);
        users.put(countId++, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int userId = user.getId();
        isExist(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int userId) {
        isExist(userId);
        return users.get(userId);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        isExist(userId);
        isExist(friendId);
        friends.get(userId).add(friendId);
        friends.get(friendId).add(userId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        isExist(userId);
        isExist(friendId);
        friends.get(userId).remove(friendId);
        friends.get(friendId).remove(userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        isExist(userId);
        List<Integer> userFriends = new ArrayList<>(friends.get(userId));
        return users.values().stream()
                .filter(user -> userFriends.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(int user1Id, int user2Id) {
        isExist(user1Id);
        isExist(user2Id);
        Set<Integer> user1Friends = friends.get(user1Id);
        Set<Integer> user2Friends = friends.get(user2Id);
        return user1Friends.stream().filter(user2Friends::contains)
                .map(users::get).collect(Collectors.toList());
    }

    void isExist(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("Пользователя с id%d не существует", userId));
        }
    }
}
