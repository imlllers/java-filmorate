MERGE INTO mpa (id, name) KEY (id) VALUES (1, 'G');
MERGE INTO mpa (id, name) KEY (id) VALUES (2, 'PG');
MERGE INTO mpa (id, name) KEY (id) VALUES (3, 'PG-13');
MERGE INTO mpa (id, name) KEY (id) VALUES (4, 'R');
MERGE INTO mpa (id, name) KEY (id) VALUES (5, 'NC-17');

MERGE INTO genres (id, name) KEY (id) VALUES (1, 'Комедия');
MERGE INTO genres (id, name) KEY (id) VALUES (2, 'Драма');
MERGE INTO genres (id, name) KEY (id) VALUES (3, 'Мультфильм');
MERGE INTO genres (id, name) KEY (id) VALUES (4, 'Триллер');
MERGE INTO genres (id, name) KEY (id) VALUES (5, 'Документальный');
MERGE INTO genres (id, name) KEY (id) VALUES (6, 'Боевик');

MERGE INTO users (id, email, login, name, birthday) KEY (id) VALUES
(1, 'user1@mail.com', 'user1', 'User One', '1990-01-01'),
(2, 'user2@mail.com', 'user2', 'User Two', '1992-02-02'),
(3, 'user3@mail.com', 'user3', 'User Three', '1995-03-03'),
(4, 'user4@mail.com', 'user4', 'User Four', '1998-04-04');

MERGE INTO films (id, name, description, releaseDate, duration, mpa_id) KEY (id) VALUES
(1, 'Крадущийся тигр, затаившийся дракон', 'Боевые искусства и философия', '2000-07-06', 120, 3),
(2, 'Крадущийся в ночи', 'Триллер про маньяка', '2014-10-31', 117, 4),
(3, 'Матрица', 'Мир иллюзий и реальности', '1999-03-31', 136, 4),
(4, 'Интерстеллар', 'Космос и время', '2014-11-07', 169, 3),
(5, 'Шрек', 'Зелёный огр и приключения', '2001-04-22', 90, 1);

MERGE INTO film_genres (film_id, genre_id) KEY (film_id, genre_id) VALUES
(1, 6),
(1, 2),
(2, 4),
(3, 6),
(3, 4),
(4, 2),
(5, 1),
(5, 3);

MERGE INTO friends (user_id, friend_id) KEY (user_id, friend_id) VALUES
(1, 2),
(2, 3),
(3, 4);

MERGE INTO likes (user_id, film_id) KEY (user_id, film_id) VALUES
(1, 1),
(2, 1),
(3, 1),

(1, 2),
(2, 2),

(1, 3),
(2, 3),
(3, 3),
(4, 3),

(2, 4),

(1, 5),
(2, 5),
(3, 5);

ALTER TABLE users ALTER COLUMN id RESTART WITH 100;
ALTER TABLE films ALTER COLUMN id RESTART WITH 100;
