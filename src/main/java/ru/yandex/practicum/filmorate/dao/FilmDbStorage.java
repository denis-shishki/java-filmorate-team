package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.SortBy;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAllFilms() {
        final String sqlQuery = "SELECT film_id, film_name, description, release_date, duration, f.mpa_id, m.mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id ";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue());
        writeGenres(film);
        writeDirector(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        final String sqlQuery = "UPDATE films " +
                "SET film_name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        deleteAllGenres(film.getId());
        writeGenres(film);
        deleteDirectors(film.getId());
        writeDirector(film);
        return film;
    }

    @Override
    public void deleteFilm(Integer filmId) {
        final String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Optional<Film> findFilm(Integer filmId) {
        final String sqlQuery = "SELECT film_id, film_name, description, release_date, duration, f.mpa_id, m.mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE film_id = ? ";
        final List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, filmId);
        return films.stream().findFirst();
    }

    @Override
    public List<Film> getTopRatedFilms(int count, Integer genreId) {
        String sqlQueryIfGenreId = "AND fg.GENRE_ID = ? ";
        if (genreId == null) sqlQueryIfGenreId = "";
        final String sqlQuery = "SELECT f.*, m.mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                sqlQueryIfGenreId +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ? ";
        if (genreId == null) {
            return jdbcTemplate.query(sqlQuery, this::makeFilm, count);
        } else {
            return jdbcTemplate.query(sqlQuery, this::makeFilm, genreId, count);
        }
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {

        final String sqlQuery = "SELECT film_id FROM likes WHERE user_id=?";

        List<Integer> likesFirstUser = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("film_id"), userId);
        List<Integer> likesSecondUser = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("film_id"), friendId);

        String[] sharedFilms = likesFirstUser.stream()
                .filter(likesSecondUser::contains)
                .map(String::valueOf)
                .toArray(String[]::new);
        String argsList = String.join(",", sharedFilms);

        String sqlQuery2 = String.format("SELECT * FROM films " +
                "JOIN mpa AS m ON films.mpa_id = m.mpa_id " +
                "WHERE film_id IN (%s)", argsList);

        return jdbcTemplate.query(sqlQuery2, this::makeFilm);
    }

    @Override
    public List<Film> getSortedFilms(Integer directorId, String sortBy) {
        final String sqlQuery = "SELECT *, COUNT(*) AS liked " +
                "FROM films AS f " +
                "JOIN film_director AS fd ON f.film_id = fd.film_id " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "WHERE fd.director_id = ?" +
                "GROUP BY f.film_id ";

        if (SortBy.YEAR.value.equals(sortBy)) {
            return getFilms(directorId, sqlQuery + "ORDER BY f.release_date");
        }
        if (SortBy.LIKES.value.equals(sortBy)) {
            return getFilms(directorId, sqlQuery + "ORDER BY liked");
        } else {
            throw new ValidationException("Некорректный параметр сортировки.");
        }
    }

    @Override
    public List<Film> getTopFilmsByGivenSearch(String query, String by) {
        final String sqlQuery = String.format("SELECT f.*, m.mpa_name FROM FILMS AS f " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_director AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "%s " +
                "GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC ", sortedSearch(query, by));
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    private List<Film> getFilms(Integer directorId, String sqlQuery) {
        return jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);
    }

    private void writeGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            film.setGenres(new HashSet<>());
            return;
        }
        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(
                "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?); ",
                genres,
                genres.size(),
                (PreparedStatement ps, Genre genre) -> {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, genre.getId());
                }
        );
    }

    private void deleteAllGenres(Integer id) {
        final String sqlQueryDelete = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryDelete, id);
    }

    private void writeDirector(Film film) {
        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            film.setDirectors(new HashSet<>());
            return;
        }
        List<Director> directors = new ArrayList<>(film.getDirectors());
        jdbcTemplate.batchUpdate(
                "INSERT INTO film_director (film_id, director_id) VALUES (?, ?); ",
                directors,
                directors.size(),
                (PreparedStatement ps, Director director) -> {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, director.getId());
                }
        );
    }

    private void deleteDirectors(Integer id) {
        final String sqlQueryDelete = "DELETE FROM film_director WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryDelete, id);
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_id", film.getMpa().getId());
        return values;
    }

    private Film makeFilm(ResultSet rs, int row) throws SQLException {
        return new Film(rs.getInt("film_id"),
                rs.getString("film_name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"))
        );
    }

    private String sortedSearch(String query, String by) {
        String qu = "'%" + query + "%'";
        String[] bySearch = by.split(",");
        if (bySearch.length == 1) {
            if (bySearch[0].contains("director")) {
                return String.format("WHERE lower(d.director_name) LIKE lower(%s)", qu);
            }
            if (bySearch[0].contains("title")) {
                return String.format("WHERE lower(f.film_name) LIKE lower(%s)", qu);
            }
        }
        return String.format("WHERE lower(f.film_name) LIKE lower(%s) OR lower(d.director_name) LIKE lower(%s)", qu, qu);
    }
}
