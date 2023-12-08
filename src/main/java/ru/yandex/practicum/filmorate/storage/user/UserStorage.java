package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public void addUser(User user);

    public void updateUser(User user);

    public List<User> getUsers();

    public User getUserById(int id);
}
