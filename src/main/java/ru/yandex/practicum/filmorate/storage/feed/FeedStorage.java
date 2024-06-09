package ru.yandex.practicum.filmorate.storage.feed;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.mapper.FeedRowMapper;

import java.util.Collection;

@Component
@AllArgsConstructor
public class FeedStorage {
    private final FeedRowMapper feedRowMapper;
    private final JdbcTemplate jdbcTemplate;

    public Collection<Feed> getAllFeed(Long userId) {
        return jdbcTemplate.query("SELECT * FROM feed WHERE user_id = ?", feedRowMapper, userId);
    }

    public void addFeed(Feed feed) {
        String sqlQuery = """
                INSERT INTO feed (entity_id, user_id, event_type, operation, timestamp)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sqlQuery,
                feed.getEntityId(),
                feed.getUserId(),
                feed.getEventType().toString(),
                feed.getOperation().toString(),
                feed.getTimestamp()
        );
    }
}
