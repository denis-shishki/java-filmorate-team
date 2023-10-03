package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeDbStorage likeDbStorage;

    public void likeFilm(Integer filmId, Integer userId) {
        likeDbStorage.likeFilm(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        likeDbStorage.deleteLike(filmId, userId);
    }
}