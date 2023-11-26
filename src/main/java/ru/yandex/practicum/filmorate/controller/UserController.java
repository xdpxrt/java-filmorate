package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int countId = 1;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на добавление пользователя" + user);
        if (checkUserInfo(user)) {
            user.setId(countId);
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
        }
        users.put(countId++, user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя" + user);
        int userId;
        if (checkUserInfo(user)) {
            if (user.getId() > 0) {
                userId = user.getId();
            } else throw new ValidationException("Неверно указан id");
            if (!users.containsKey(userId)) {
                throw new ValidationException("Пользователя с id:" + userId + " не существует");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(userId, user);
        }
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен запрос на списко пользователей");

        return new ArrayList<>(users.values());
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
