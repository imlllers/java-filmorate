package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class EventServiceIntegrationTest {

    private final EventService eventService;
    private final UserDbStorage userStorage;

    @Test
    void createLikeEventAndGetUserFeed() {
        User user = createTestUser("like@mail.ru", "likeTest");
        eventService.createEvent(user.getId(), EventType.LIKE, Operation.ADD, 101);

        List<?> feed = eventService.getUserFeed(user.getId());
        assertThat(feed).hasSize(1);
        assertThat(feed.get(0)).extracting("eventType").isEqualTo(EventType.LIKE.name());
        assertThat(feed.get(0)).extracting("operation").isEqualTo(Operation.ADD.name());
    }

    @Test
    void createFriendEvents() {
        User user = createTestUser("friend@mail.ru", "friendTest");
        eventService.createEvent(user.getId(), EventType.FRIEND, Operation.ADD, 201);
        eventService.createEvent(user.getId(), EventType.FRIEND, Operation.REMOVE, 201);

        List<?> feed = eventService.getUserFeed(user.getId());
        assertThat(feed).hasSize(2);
        assertThat(feed)
                .extracting("eventType")
                .containsOnly(EventType.FRIEND.name());

        assertThat(feed)
                .extracting("operation")
                .contains(Operation.ADD.name(), Operation.REMOVE.name());
    }

    @Test
    void createReviewEvents() {
        User user = createTestUser("review@mail.ru", "reviewTest");
        eventService.createEvent(user.getId(), EventType.REVIEW, Operation.ADD, 301);
        eventService.createEvent(user.getId(), EventType.REVIEW, Operation.UPDATE, 301);
        eventService.createEvent(user.getId(), EventType.REVIEW, Operation.REMOVE, 301);

        List<?> feed = eventService.getUserFeed(user.getId());
        assertThat(feed).hasSize(3);
        assertThat(feed)
                .extracting("eventType")
                .containsOnly(EventType.REVIEW.name());

        assertThat(feed)
                .extracting("operation")
                .contains(Operation.ADD.name(), Operation.UPDATE.name(), Operation.REMOVE.name());
    }

    @Test
    void createMultipleEventsAndCheckOrder() {
        User user = createTestUser("multi@mail.ru", "multiTest");

        eventService.createEvent(user.getId(), EventType.FRIEND, Operation.ADD, 401);
        eventService.createEvent(user.getId(), EventType.LIKE, Operation.ADD, 501);
        eventService.createEvent(user.getId(), EventType.REVIEW, Operation.ADD, 601);

        List<?> feed = eventService.getUserFeed(user.getId());
        assertThat(feed).hasSize(3);

        assertThat(feed.get(0)).extracting("eventType").isEqualTo(EventType.FRIEND.name());
        assertThat(feed.get(1)).extracting("eventType").isEqualTo(EventType.LIKE.name());
        assertThat(feed.get(2)).extracting("eventType").isEqualTo(EventType.REVIEW.name());
    }

    private User createTestUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName("Test User");
        user.setBirthday(java.time.LocalDate.of(1990, 1, 1));
        return userStorage.create(user);
    }
}