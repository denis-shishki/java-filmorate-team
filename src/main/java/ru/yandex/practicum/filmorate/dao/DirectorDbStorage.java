package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> findAllDirectors() {
        final String sqlQuery = "SELECT director_id, director_name FROM directors";
        return jdbcTemplate.query(sqlQuery, this::makeDirector);
    }

    @Override
    public Optional<Director> findDirectorById(Integer id) {
        final String sqlQuery = "SELECT director_id, director_name FROM directors WHERE director_id = ?";
        final List<Director> directors = jdbcTemplate.query(sqlQuery, this::makeDirector, id);
        return directors.stream().findFirst();
    }

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        director.setId(simpleJdbcInsert.executeAndReturnKey(toMap(director)).intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        final String sqlQuery = "UPDATE directors SET director_name = ? WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(Integer directorId) {
        final String sqlQuery = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, directorId);
    }

    @Override
    public void setDirectors(List<Film> films) {
        if (films == null || films.isEmpty()) return;
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, (f) -> f));

        String createTable = "CREATE TEMPORARY TABLE temp_table (film_id INT)";
        String addFilmIdsInTable = "INSERT INTO temp_table (film_id) values (?)";
        String deleteTable = "DROP TABLE temp_table";

        jdbcTemplate.execute(createTable);

        jdbcTemplate.batchUpdate(addFilmIdsInTable,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Film film = films.get(i);
                        ps.setInt(1, film.getId());
                    }

                    public int getBatchSize() {
                        return films.size();
                    }
                });

        jdbcTemplate.query(
                "SELECT fd.film_id, fd.director_id, d.director_name " +
                "FROM film_director AS fd " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "JOIN temp_table AS t ON fd.film_id = t.film_id",
                (rs) -> {
                    final Film film = filmById.get(rs.getInt("film_id"));

                    if (film.getDirectors() != null) {
                        film.addDirector(new Director(rs.getInt("director_id"), rs.getString("director_name")));
                    }
                });

        jdbcTemplate.execute(deleteTable);
    }

    private Director makeDirector(ResultSet rs, int row) throws SQLException {
        return new Director(rs.getInt("director_id"), rs.getString("director_name"));
    }

    private Map<String, Object> toMap(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("director_name", director.getName());
        return values;
    }
}
