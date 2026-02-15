package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
@AllArgsConstructor
public class MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> findAll() {
        String sql = "SELECT id, name FROM mpa ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mpa m = new Mpa();
            m.setId(rs.getInt("id"));
            m.setName(rs.getString("name"));
            return m;
        });
    }

    public Mpa findById(Integer id) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?";
        List<Mpa> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mpa m = new Mpa();
            m.setId(rs.getInt("id"));
            m.setName(rs.getString("name"));
            return m;
        }, id);

        if (list.isEmpty()) {
            throw new NotFoundException("MPA с id=" + id + " не найден");
        }
        return list.getFirst();
    }
}
