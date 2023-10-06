package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final UserService userService;
    private final DirectorService directorService;

    public List<Film> findAllFilms() {
        List<Film> allFilms = filmStorage.findAllFilms();
        genreService.setGenres(allFilms);
        directorService.setDirectors(allFilms);
        return allFilms;
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        checkFilm(film.getId());
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Integer id) {
        checkFilm(id);
        filmStorage.deleteFilm(id);
    }

    public Film findFilm(Integer id) {
        Film film = filmStorage.findFilm(id).orElseThrow(
                () -> new FilmNotFoundException("Фильм не найден")
        );
        genreService.setGenres(List.of(film));
        directorService.setDirectors(List.of(film));
        return film;
    }

    public List<Film> getTopRatedFilms(int count) {
        return filmStorage.getTopRatedFilms(count);
    }

    private void validateFilm(Film film) {
        if (film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        } else if (film.getDuration() < 1) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

    public List<Film> getTopFilmsByGivenSearch(String query, String by) {
        List<Film> filmsByGivenSearch = filmStorage.getTopFilmsByGivenSearch(query, by);
        genreService.setGenres(filmsByGivenSearch);
        directorService.setDirectors(filmsByGivenSearch);
        return filmsByGivenSearch;
    }

    private void checkFilm(Integer id) {
        if (filmStorage.findFilm(id).isEmpty()) {
            throw new FilmNotFoundException("Фильм не найден.");
        }
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        User user = userService.findUser(userId);
        User friend = userService.findUser(friendId);

        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getSortedFilms(Integer directorId, String sortBy) {
        List<Film> sortedFilms = filmStorage.getSortedFilms(directorId, sortBy);
        if (sortedFilms.isEmpty()) {
            throw new DirectorNotFoundException("Режиссёр не найден.");
        }
        directorService.setDirectors(sortedFilms);
        genreService.setGenres(sortedFilms);
        return sortedFilms;
    }
}
