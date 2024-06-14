package ru.yandex.practicum.filmorate.storage.dataBase.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.mapper.FeedRowMapper;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery.FeedQuery;

import java.util.Collection;

@Component
@AllArgsConstructor
public class FeedRepository {
    private final FeedRowMapper feedRowMapper;
    private final JdbcTemplate jdbcTemplate;

    public Collection<Feed> getAllFeedByUserId(Long userId) {
        return jdbcTemplate.query(FeedQuery.GET_ALL_FEED_BY_USER_ID, feedRowMapper, userId);
    }

    public void addFeed(Feed feed) {
        String sqlQuery = FeedQuery.ADD_FEED;

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
