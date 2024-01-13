package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface MPADao {
    MPA getMPA(int id);

    List<MPA> getAllMPA();
}
