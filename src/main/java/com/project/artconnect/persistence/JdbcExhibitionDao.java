package com.project.artconnect.persistence;

import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcExhibitionDao implements ExhibitionDao {

    @Override
    public List<Exhibition> findAll() {
        List<Exhibition> exhibitions = new ArrayList<>();
        String sql = "SELECT e.title, e.start_date, e.end_date, e.description, e.curator_name, e.theme, g.name AS gallery_name "
                + "FROM exhibitions e JOIN galleries g ON e.gallery_id = g.id ORDER BY e.start_date";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                exhibitions.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all exhibitions", e);
        }
        return exhibitions;
    }

    @Override
    public void save(Exhibition exhibition) {
        String getGalleryId = "SELECT id FROM galleries WHERE name = ?";
        String insertSql = "INSERT INTO exhibitions (gallery_id, title, start_date, end_date, description, curator_name, theme) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long galleryId = requireGalleryId(conn, getGalleryId, exhibition.getGallery().getName());
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setLong(1, galleryId);
                    ps.setString(2, exhibition.getTitle());
                    ps.setDate(3, Date.valueOf(exhibition.getStartDate()));
                    ps.setDate(4, Date.valueOf(exhibition.getEndDate()));
                    ps.setString(5, exhibition.getDescription());
                    ps.setString(6, exhibition.getCuratorName());
                    ps.setString(7, exhibition.getTheme());
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving exhibition: " + exhibition.getTitle(), e);
        }
    }

    @Override
    public void update(Exhibition exhibition) {
        String updateSql = "UPDATE exhibitions SET start_date=?, end_date=?, description=?, curator_name=?, theme=? WHERE title=?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setDate(1, Date.valueOf(exhibition.getStartDate()));
            ps.setDate(2, Date.valueOf(exhibition.getEndDate()));
            ps.setString(3, exhibition.getDescription());
            ps.setString(4, exhibition.getCuratorName());
            ps.setString(5, exhibition.getTheme());
            ps.setString(6, exhibition.getTitle());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating exhibition: " + exhibition.getTitle(), e);
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM exhibitions WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting exhibition: " + title, e);
        }
    }

    private Exhibition mapRow(ResultSet rs) throws SQLException {
        Exhibition exhibition = new Exhibition();
        exhibition.setTitle(rs.getString("title"));
        exhibition.setStartDate(rs.getDate("start_date").toLocalDate());
        exhibition.setEndDate(rs.getDate("end_date").toLocalDate());
        exhibition.setDescription(rs.getString("description"));
        exhibition.setCuratorName(rs.getString("curator_name"));
        exhibition.setTheme(rs.getString("theme"));
        Gallery gallery = new Gallery();
        gallery.setName(rs.getString("gallery_name"));
        exhibition.setGallery(gallery);
        return exhibition;
    }

    private long requireGalleryId(Connection conn, String sql, String galleryName) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, galleryName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        throw new SQLException("Gallery not found: " + galleryName);
    }
}
