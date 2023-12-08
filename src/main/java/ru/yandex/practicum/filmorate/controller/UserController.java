package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на добавление пользователя" + user);
        if (checkUserInfo(user)) {
            userStorage.addUser(user);
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя" + user);
        if (checkUserInfo(user)) {
            userStorage.updateUser(user);
        }
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен запрос на списко пользователей");
        return userStorage.getUsers();
    }

    private boolean checkUserInfo(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("На входе пустой объект!");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелы!");
        }
        return true;
    }
}
