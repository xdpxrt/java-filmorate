package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MPAController {

    private final MPAService mpaService;

    @Autowired
    public MPAController(MPAService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{id}")
    public MPA getMPA(@PathVariable int id) {
        log.info("Получен запрос на получение рейтинга по id: " + id);
        if (id < 1) {
            throw new IncorrectParameterException("id");
        }
        return mpaService.getMPA(id);
    }

    @GetMapping
    public List<MPA> getAllMPA() {
        log.info("Получен запрос на получение списка рейтингов");
        return mpaService.getAllMPA();
    }
}
