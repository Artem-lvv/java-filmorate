package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.FeedRepository;
import ru.yandex.practicum.filmorate.storage.dataBase.dao.UserRepository;

import java.util.Collection;

@Component
@AllArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    public Collection<Feed> getAllFeed(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundByIdException("User", userId.toString()));

        return feedRepository.getAllFeedByUserId(userId);
    }

    public void addFeed(Feed feed) {
        feedRepository.addFeed(feed);
    }
}
