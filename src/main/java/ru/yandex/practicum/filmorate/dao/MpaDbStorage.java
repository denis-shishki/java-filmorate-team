package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAllMpa() {
        String sqlQuery = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlQuery, this::map);
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        String sqlQuery = "SELECT * FROM mpa WHERE mpa_id = ?";
        List<Mpa> mpa = jdbcTemplate.query(sqlQuery, this::map, id);
        return mpa.isEmpty() ? Optional.empty() : Optional.of(mpa.get(0));
    }

    private Mpa map(ResultSet rs, int row) throws SQLException {
        return new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));
    }
}
