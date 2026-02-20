package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findById(Integer id) {
        return directorStorage.findById(id);
    }

    public Director create(Director director) {
        validateDirector(director);
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        if (director.getId() == null) {
            throw new ValidationException("ID director не может быть null");
        }
        validateDirector(director);
        return directorStorage.update(director);
    }

    public void delete(Integer id) {
        directorStorage.delete(id);
    }

    private void validateDirector(Director director) {
        if (director.getName() == null || director.getName().trim().isEmpty()) {
            throw new ValidationException("Имя director не может быть пустым");
        }
    }
}