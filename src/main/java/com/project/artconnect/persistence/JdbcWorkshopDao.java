package com.project.artconnect.persistence;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcWorkshopDao implements WorkshopDao {

    @Override
    public Optional<Workshop> findById(Long id) {
        String sql = "SELECT w.id, w.title, w.workshop_date, w.duration_minutes, w.max_participants, "
                + "w.price, w.location, w.description, w.level, a.name AS instructor_name "
                + "FROM workshops w JOIN artists a ON w.instructor_artist_id = a.id WHERE w.id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding workshop by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Workshop> findAll() {
        List<Workshop> workshops = new ArrayList<>();
        String sql = "SELECT w.id, w.title, w.workshop_date, w.duration_minutes, w.max_participants, "
                + "w.price, w.location, w.description, w.level, a.name AS instructor_name "
                + "FROM workshops w JOIN artists a ON w.instructor_artist_id = a.id ORDER BY w.workshop_date";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                workshops.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all workshops", e);
        }
        return workshops;
    }

    private Workshop mapRow(ResultSet rs) throws SQLException {
        Workshop workshop = new Workshop();
        workshop.setTitle(rs.getString("title"));
        Timestamp ts = rs.getTimestamp("workshop_date");
        if (ts != null) workshop.setDate(ts.toLocalDateTime());
        workshop.setDurationMinutes(rs.getInt("duration_minutes"));
        workshop.setMaxParticipants(rs.getInt("max_participants"));
        workshop.setPrice(rs.getDouble("price"));
        workshop.setLocation(rs.getString("location"));
        workshop.setDescription(rs.getString("description"));
        workshop.setLevel(rs.getString("level"));
        Artist instructor = new Artist();
        instructor.setName(rs.getString("instructor_name"));
        workshop.setInstructor(instructor);
        return workshop;
    }
}
