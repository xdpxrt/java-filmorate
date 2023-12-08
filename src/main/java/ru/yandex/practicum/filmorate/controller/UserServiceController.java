package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class UserServiceController {
    private final UserService userService;

    public UserServiceController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        isAcceptable(id, "id");
        isAcceptable(friendId, "friendId");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        isAcceptable(id, "id");
        isAcceptable(friendId, "friendId");
        userService.removeFriend(id, friendId);

    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        isAcceptable(id, "id");
        return userService.getFriends(id);

    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getFriends(@PathVariable int id, int otherId) {
        isAcceptable(id, "id");
        isAcceptable(otherId, "otherId");
        return userService.getMutualFriends(id, otherId);
    }

    private boolean isAcceptable(int id, String param) {
        if (id <= 0) {
            throw new IncorrectParameterException(param);
        }
        return true;
    }
}
