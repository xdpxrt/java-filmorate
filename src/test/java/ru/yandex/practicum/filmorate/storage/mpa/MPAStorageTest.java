package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MPAStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private MPAStorage mpaStorage;

    @BeforeEach
    public void init() {
        mpaStorage = new MPAStorage(jdbcTemplate);
    }

    @Test
    public void getMPAByIdTest() {
        MPA mpaFromStorage = mpaStorage.getMPA(1);
        assertEquals(1, mpaFromStorage.getId());
    }

    @Test
    public void getMPAByIdWithWrongIdTest() {
        assertThrows(NotFoundException.class,
                () -> mpaStorage.getMPA(0), "Рейтинга с id0 не существует");
    }

    @Test
    public void getAllMPATest() {
        MPA mpa1 = mpaStorage.getMPA(1);
        MPA mpa3 = mpaStorage.getMPA(3);
        MPA mpa5 = mpaStorage.getMPA(5);
        List<MPA> mpaFromStorage = mpaStorage.getAllMPA();
        assertEquals(mpa1, mpaFromStorage.get(0));
        assertEquals(mpa3, mpaFromStorage.get(2));
        assertEquals(mpa5, mpaFromStorage.get(4));
    }
}
