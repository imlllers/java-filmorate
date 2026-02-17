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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, LikesStorage.class, UserDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final LikesStorage likesStorage;
    private final UserDbStorage userStorage;

    @Test
    void createAndFindByIdWithGenres() {
        User user = new User();
        user.setEmail("film-like@mail.ru");
        user.setLogin("filmLike");
        user.setName("Film Like");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userStorage.create(user);

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1);
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        film.setGenres(genres);

        Film created = filmStorage.create(film);
        likesStorage.addLike(created.getId(), createdUser.getId());
        Film found = filmStorage.findById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getGenres()).hasSize(1);
        assertThat(found.getLikes()).contains(createdUser.getId());
    }

    @Test
    void updateFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film created = filmStorage.create(film);
        created.setName("Updated");
        Film updated = filmStorage.update(created);

        assertThat(updated.getName()).isEqualTo("Updated");
    }

    @Test
    void findAllFilms() {
        Film film = new Film();
        film.setName("Film2");
        film.setDescription("Desc2");
        film.setReleaseDate(LocalDate.of(2001, 1, 1));
        film.setDuration(90);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        filmStorage.create(film);
        Collection<Film> films = filmStorage.findAll();

        assertThat(films).isNotEmpty();
    }
}
