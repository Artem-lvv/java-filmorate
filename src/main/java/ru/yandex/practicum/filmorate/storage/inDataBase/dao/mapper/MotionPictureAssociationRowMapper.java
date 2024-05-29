package ru.yandex.practicum.filmorate.storage.inDataBase.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MotionPictureAssociationRowMapper implements RowMapper<MPA> {
    @Override
    public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
        return MPA.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .build();
    }
}
