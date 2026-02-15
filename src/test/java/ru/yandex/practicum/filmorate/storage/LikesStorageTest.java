package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, FilmDbStorage.class, LikesStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LikesStorageTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final LikesStorage likesStorage;

    @Test
    void addAndRemoveLike() {
        User user = new User();
        user.setEmail("like@mail.ru");
        user.setLogin("like");
        user.setName("Like");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userStorage.create(user);

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        Film createdFilm = filmStorage.create(film);

        likesStorage.addLike(createdFilm.getId(), createdUser.getId());
        List<Integer> likes = likesStorage.getLikesUserIds(createdFilm.getId());
        assertThat(likes).contains(createdUser.getId());
        assertThat(likesStorage.countLikes(createdFilm.getId())).isEqualTo(1);

        likesStorage.removeLike(createdFilm.getId(), createdUser.getId());
        List<Integer> afterRemove = likesStorage.getLikesUserIds(createdFilm.getId());
        assertThat(afterRemove).doesNotContain(createdUser.getId());
    }
}
