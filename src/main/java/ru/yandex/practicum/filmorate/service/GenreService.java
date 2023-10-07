package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> findAllGenres() {
        return genreStorage.findAllGenres();
    }

    public Genre findGenreById(Integer genreId) {
        return genreStorage.findGenreById(genreId).orElseThrow(() -> new GenreNotFoundException("Жанр не найден."));
    }

    public Genre createGenre(Genre genre) {
        return genreStorage.createGenre(genre);
    }

    public void setGenres(List<Film> films) {
        genreStorage.setGenres(films);
    }
}
