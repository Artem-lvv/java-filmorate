-- Создаем таблицу genre
CREATE TABLE IF NOT EXISTS genre
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT
);

-- Создаем таблицу motion_picture_association
CREATE TABLE IF NOT EXISTS motion_picture_association
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT
);

-- Создаем таблицу users
CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,
    login    VARCHAR(255) NOT NULL UNIQUE,
    name     VARCHAR(255),
    birthday DATE
);

-- Создаем таблицу film
CREATE TABLE IF NOT EXISTS film
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    release_date DATE,
    duration     INTEGER
);

-- Создаем таблицу для связи film и likes (многие ко многим)
CREATE TABLE IF NOT EXISTS film_likes
(
    film_id BIGINT REFERENCES film (id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

-- Создаем таблицу для связи film и genre (многие ко многим)
CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  BIGINT REFERENCES film (id) ON DELETE CASCADE,
    genre_id BIGINT REFERENCES genre (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

-- Создаем таблицу для связи film и motion_picture_association (многие ко многим)
CREATE TABLE IF NOT EXISTS film_mpa
(
    film_id BIGINT REFERENCES film (id) ON DELETE CASCADE,
    mpa_id  BIGINT REFERENCES motion_picture_association (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, mpa_id)
);

-- Создаем таблицу для связи user и friends (многие ко многим)
CREATE TABLE IF NOT EXISTS user_friends
(
    user_id   BIGINT REFERENCES users (id) ON DELETE CASCADE,
    friend_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, friend_id)
);

-- Создаем таблицу для friend requests
CREATE TABLE IF NOT EXISTS friend_requests
(
    user_id   BIGINT REFERENCES users (id) ON DELETE CASCADE,
    friend_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    status    VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, friend_id)
);
