package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component("inMemoryRatingStorage")
public class inMemoryRatingStorage implements RatingStorage {

    Map<Long, Rating> ratingMap = new HashMap<>();

    @Override
    public Rating createRating(Rating rating) {
        rating.setId(getRatingNextId());
        ratingMap.put(rating.getId(), rating);
        return rating;
    }

    @Override
    public Rating modifyRating(Rating rating) {
        ratingMap.put(rating.getId(), rating);
        return rating;
    }

    @Override
    public Rating deleteRating(Rating rating) {
        ratingMap.remove(rating.getId());
        return rating;
    }

    @Override
    public List<Rating> getAllRating() {
        return ratingMap.values().stream().toList();
    }

    @Override
    public Optional<Rating> findById(Long ratingId) {
        return Optional.ofNullable(ratingMap.get(ratingId));
    }

    @Override
    public Optional<Rating> findByName(String name) {
        return ratingMap.values().stream()
                .filter(rating -> rating.getName().equals(name))
                .findFirst();
    }

    public long getRatingNextId() {
        long currentMaxId = ratingMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
