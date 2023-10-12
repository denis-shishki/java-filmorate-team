package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final LikeService likeService;

    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable("filmId") Integer filmId) {
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/{filmId}")
    public Film findFilm(@PathVariable Integer filmId) {
        return filmService.findFilm(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void likeFilm(@PathVariable Integer filmId, @PathVariable Integer userId) {
        likeService.likeFilm(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        likeService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopRatedFilms(@RequestParam(defaultValue = "10", required = false) @Positive int count,
                                       @RequestParam(required = false) Integer genreId,
                                       @RequestParam(required = false) String year) {
        return filmService.getTopRatedFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(name = "userId") Integer userId,
                                     @RequestParam(name = "friendId") Integer friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getSortedFilms(@PathVariable Integer directorId, @RequestParam String sortBy) {
        return filmService.getSortedFilms(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> getTopFilmsByGivenSearch(@RequestParam(name = "query") String query,
                                               @RequestParam(name = "by", defaultValue = "") String by) {
        return filmService.getTopFilmsByGivenSearch(query, by);
    }
}
