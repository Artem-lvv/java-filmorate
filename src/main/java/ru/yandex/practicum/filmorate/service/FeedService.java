package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.util.Collection;

@Component
@AllArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;

    public Collection<Feed> getAllFeed(Long userId) {
        return feedStorage.getAllFeed(userId);
    }

    public void addFeed(Feed feed) {
        feedStorage.addFeed(feed);
    }
}
