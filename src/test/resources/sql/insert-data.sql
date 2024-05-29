-- Вставка данных в таблицу genre
INSERT INTO genre (name, description)
VALUES ('Action', 'Action-packed movies with thrilling sequences'),
       ('Comedy', 'Movies that are designed to make the audience laugh'),
       ('Drama', 'Movies with a serious tone and storyline');

-- Вставка данных в таблицу motion_picture_association
INSERT INTO motion_picture_association (name, description)
VALUES ('G', 'General Audience'),
       ('PG', 'Parental Guidance Suggested'),
       ('R', 'Restricted');

-- Вставка данных в таблицу users
INSERT INTO users (id, email, login, name, birthday)
VALUES (0, 'john.doe@example.com', 'johndoe', 'John Doe', '1980-01-01'),
       (1, 'jane.smith@example.com', 'janesmith', 'Jane Smith', '1990-05-15'),
       (2, 'bob.johnson@example.com', 'bobjohnson', 'Bob Johnson', '1985-03-22');

-- Вставка данных в таблицу film
INSERT INTO film (name, description, release_date, duration)
VALUES ('The Great Adventure', 'An action movie with stunning visuals', '2023-06-15', 120),
       ('Funny Moments', 'A comedy movie that will make you laugh out loud', '2023-08-20', 90),
       ('Serious Matters', 'A drama movie with a gripping storyline', '2023-11-05', 110);

-- Вставка данных в таблицу film_likes
INSERT INTO film_likes (film_id, user_id)
VALUES (1, 0),
       (1, 1),
       (2, 0),
       (3, 2);

-- Вставка данных в таблицу film_genre
INSERT INTO film_genre (film_id, genre_id)
VALUES (1, 1), -- The Great Adventure is an Action movie
       (2, 2), -- Funny Moments is a Comedy movie
       (3, 3);
-- Serious Matters is a Drama movie

-- Вставка данных в таблицу film_mpa
INSERT INTO film_mpa (film_id, mpa_id)
VALUES (1, 2), -- The Great Adventure is PG
       (2, 1), -- Funny Moments is G
       (3, 3);
-- Serious Matters is R

-- Вставка данных в таблицу user_friends
INSERT INTO user_friends (user_id, friend_id)
VALUES (1, 1), -- John Doe and Jane Smith are friends
       (2, 2);
-- Jane Smith and Bob Johnson are friends

-- Вставка данных в таблицу friend_requests
INSERT INTO friend_requests (user_id, friend_id, status)
VALUES (2, 1, 'CONFIRMED'); -- Bob Johnson sent a friend request to John Doe, pending
