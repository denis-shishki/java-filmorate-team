package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAllGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    @Override
    public Optional<Genre> findGenreById(Integer id) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genre = jdbcTemplate.query(sqlQuery, this::makeGenre, id);
        return genre.isEmpty() ? Optional.empty() : Optional.of(genre.get(0));
    }

    @Override
    public Genre createGenre(Genre genre) {
        String sqlQuery = "INSERT INTO genres (genre_name) VALUES (?)";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> toMap(rs), genre.getId());
    }

    @Override
    public void setGenres(List<Film> films) {
        final String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, (f) -> f));
        if (inSql.isEmpty()) return;
        jdbcTemplate.query(
                String.format("SELECT fg.film_id, fg.genre_id, gn.genre_name " +
                        "FROM film_genre AS fg " +
                        "LEFT JOIN genres AS gn ON fg.genre_id = gn.genre_id " +
                        "WHERE fg.film_id IN (%s)", inSql),
                (rs) -> {
                    final Film film = filmById.get(rs.getInt("film_id"));
                    film.addGenre(new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
                },
                films.stream().map(Film::getId).toArray());
    }

    private Genre toMap(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }

    private Genre makeGenre(ResultSet rs, int row) throws SQLException {
        return new Genre(rs.getInt("genre_id"),
                rs.getString("genre_name")
        );
    }
}