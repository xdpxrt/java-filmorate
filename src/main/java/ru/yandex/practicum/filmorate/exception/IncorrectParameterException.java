package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class IncorrectParameterException extends RuntimeException {
    private final String parameter;

    public IncorrectParameterException(String parameter) {
        this.parameter = parameter;
    }
}
