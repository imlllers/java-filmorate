package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(MpaStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaStorageTest {
    private final MpaStorage mpaStorage;

    @Test
    void findAllMpa() {
        List<Mpa> mpa = mpaStorage.findAll();
        assertThat(mpa).hasSize(5);
    }

    @Test
    void findMpaById() {
        Mpa mpa = mpaStorage.findById(1);
        assertThat(mpa.getId()).isEqualTo(1);
        assertThat(mpa.getName()).isNotBlank();
    }
}
