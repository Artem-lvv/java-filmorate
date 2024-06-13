package ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery;

public class FeedQuery {
    private FeedQuery() {
    }

    public static final String GET_ALL_FEED_BY_USER_ID = "SELECT * FROM feed WHERE user_id = ?";
    public static final String ADD_FEED = """
            INSERT INTO feed (entity_id, user_id, event_type, operation, timestamp)
            VALUES (?, ?, ?, ?, ?)
            """;
}
