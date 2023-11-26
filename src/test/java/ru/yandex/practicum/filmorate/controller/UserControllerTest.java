package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private Set<ConstraintViolation<User>> violations;
    private UserController userController;
    private User user;

    @BeforeEach
    public void init() {
        userController = new UserController();
    }

    @Test
    public void userCreateTest() {
        user = new User("user@yandex.ru", "user", "User",
                LocalDate.of(1991, 10, 10));
        violations = validator.validate(user);
        userController.addUser(user);
        assertEquals(user, userController.getUsers().get(0));
        assertEquals(0, violations.size());
    }

    @Test
    public void userCreateBadLoginTest() {
        user = new User("user@yandex.ru", "new user", "User",
                LocalDate.of(1991, 10, 10));
        assertThrows(ValidationException.class,
                () -> userController.addUser(user),
                "Логин не должен содержать пробелы!");
    }

    @Test
    public void userCreateBadEmailTest() {
        user = new User("user@.ru", "user", "User",
                LocalDate.of(1991, 10, 10));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void userCreateBadBirthdayTest() {
        user = new User("user@yandex.ru", "user", "User",
                LocalDate.of(2042, 10, 10));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void userCreateEmptyNameTest() {
        user = new User("user@yandex.ru", "user", "",
                LocalDate.of(1991, 10, 10));
        violations = validator.validate(user);
        userController.addUser(user);
        assertEquals(user, userController.getUsers().get(0));
    }

    @Test
    public void userUpdateTest() {
        userController.addUser(new User("user@yandex.ru", "user", "User",
                LocalDate.of(1991, 10, 10)));
        user = new User(1, "newuser@yandex.ru", "newuser", "newUser",
                LocalDate.of(1991, 11, 10));
        userController.updateUser(user);
        violations = validator.validate(user);
        assertEquals(user, userController.getUsers().get(0));
        assertEquals(0, violations.size());
    }

    @Test
    public void userUpdateUnknownTest() {
        user = new User(1, "user@yandex.ru", "user", "User",
                LocalDate.of(1991, 10, 10));
        violations = validator.validate(user);
        assertEquals(0, violations.size());
        assertThrows(ValidationException.class,
                () -> userController.updateUser(user),
                "Пользователя с id:1 не существует");
    }

    @Test
    public void userGetAllTest() {
        userController.addUser(new User("user@yandex.ru", "user", "User",
                LocalDate.of(1991, 10, 10)));
        userController.addUser(new User("user@yandex.ru", "user", "User",
                LocalDate.of(1991, 10, 10)));
        assertEquals(2, userController.getUsers().size());
    }

    @Test
    public void emptyUserAddTest() {
        assertThrows(ValidationException.class,
                () -> userController.addUser(user),
                "На входе пустой объект!");
    }

    @Test
    public void emptyUserUpdateTest() {
        assertThrows(ValidationException.class,
                () -> userController.updateUser(user),
                "На входе пустой объект!");
    }

}
