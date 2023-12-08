package ru.yandex.practicum.filmorate.storage.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int countId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void addUser(User user) {
        user.setId(countId);
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        users.put(countId++, user);
    }

    @Override
    public void updateUser(User user) {
        int userId = user.getId();
        if (user.getId() <= 0) {
            throw new ValidationException("Неверно указан id");
        }
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(String.format("Пользователя с id%d не существует", userId));
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        users.put(userId, user);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            return null;
        }
        return users.get(id);
    }
}
