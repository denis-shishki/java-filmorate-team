package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Integer filmId);

    Optional<Film> findFilm(Integer filmId);

    List<Film> getTopRatedFilms(int count);

    List<Film> getCommonFilms(Integer userId, Integer friendId);
}
