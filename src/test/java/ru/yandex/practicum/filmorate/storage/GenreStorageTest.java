package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreStorage.class, FilmDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreStorageTest {
    private final GenreStorage genreStorage;
    private final FilmDbStorage filmStorage;

    @Test
    void findAllGenres() {
        List<Genre> genres = genreStorage.findAll();
        assertThat(genres).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findGenreById() {
        Genre genre = genreStorage.findById(1);
        assertThat(genre.getId()).isEqualTo(1);
        assertThat(genre.getName()).isNotBlank();
    }

    @Test
    void findGenresByFilmId() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1);
        film.setGenres(new HashSet<>(List.of(genre)));

        Film created = filmStorage.create(film);
        assertThat(genreStorage.findByFilmId(created.getId())).hasSize(1);
    }
}
