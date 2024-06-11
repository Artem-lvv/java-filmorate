package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundByIdException;
import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.inDataBase.dao.UserRepository;

import java.util.Collection;

@Component
@AllArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;
    private final UserRepository userRepository;

    public Collection<Feed> getAllFeed(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundByIdException("User", userId.toString()));

        return feedStorage.getAllFeed(userId);
    }

    public void addFeed(Feed feed) {
        feedStorage.addFeed(feed);
    }
}
