package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAllFilms();

    List<Film> findFilmsByIds(List<Integer> filmIds);

    List<Integer> findLikeFilmIdsByUserId(int userId);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Integer filmId);

    Optional<Film> findFilm(Integer filmId);

    List<Film> getTopRatedFilms(int count, Integer genreId, String year);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> getSortedFilms(Integer directorId, String sortBy);

    List<Film> getTopFilmsByGivenSearch(String query, String by);

}