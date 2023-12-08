package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на добавление пользователя" + user);
        checkUserInfo(user);
        userStorage.addUser(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя" + user);
        checkUserInfo(user);
        userStorage.updateUser(user);
        return user;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("Получен запрос на список пользователей");
        return userStorage.getUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("Получен запрос на получение пользователя по id");
        isAcceptable(id, "id");
        return userStorage.getUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен запрос на добавление друга");
        isAcceptable(id, "id");
        isAcceptable(friendId, "friendId");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен запрос на удаление друга");
        isAcceptable(id, "id");
        isAcceptable(friendId, "friendId");
        userService.removeFriend(id, friendId);

    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Получен запрос на получение списка друзей");
        isAcceptable(id, "id");
        return userService.getFriends(id);

    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получен запрос на получение списка общих друзей");
        isAcceptable(id, "id");
        isAcceptable(otherId, "otherId");
        return userService.getMutualFriends(id, otherId);
    }

    private void isAcceptable(int id, String param) {
        if (id < 1) {
            throw new IncorrectParameterException(param);
        }
    }

    private void checkUserInfo(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("На входе пустой объект!");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелы!");
        }
    }
}
