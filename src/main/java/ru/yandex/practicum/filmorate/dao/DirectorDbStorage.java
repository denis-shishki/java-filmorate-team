package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getAllDirectors() {
        final String sqlQuery = "SELECT director_id, director_name FROM directors";
        return jdbcTemplate.query(sqlQuery, this::makeDirector);
    }

    @Override
    public Optional<Director> findDirector(int directorId) {
        final String sqlQuery = "SELECT director_id, director_name FROM directors WHERE director_id = ?";
        final List<Director> directors = jdbcTemplate.query(sqlQuery, this::makeDirector, directorId);

        if (directors.isEmpty()) return Optional.empty();
        return directors.stream().findFirst();
    }

    @Override
    public Director createDirector(Director director) {
        final String sqlQuery = "INSERT INTO directors (director_name) VALUES (?)";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> toMap(rs), director.getId());
    }

    @Override
    public Director updateDirector(Director director) {
        final String sqlQuery = "UPDATE directors SET director_name = ? WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(int directorId) {
        final String sqlQuery = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, directorId);
    }

    @Override
    public void setDirectors(List<Film> films) {
        final String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, (f) -> f));
        if (inSql.isEmpty()) return;

        jdbcTemplate.query(String.format(
                "SELECT fd.film_id, fd.director_id, d.director_name " +
                        "FROM film_director AS fd " +
                        "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                        "WHERE fd.film_id IN (%s)", inSql), (rs) -> {
            final Film film = filmById.get(rs.getInt("film_id"));

            if (film.getDirectors() != null) {
                film.addDirector(new Director(rs.getInt("director_id"), rs.getString("director_name")));
            }
        }, films.stream().map(Film::getId).toArray());
    }

    private Director makeDirector(ResultSet rs, int row) throws SQLException {
        return new Director(rs.getInt("director_id"), rs.getString("director_name"));
    }

    private Director toMap(ResultSet rs) throws SQLException {
        return new Director(rs.getInt("director_id"), rs.getString("director_name"));
    }
}
