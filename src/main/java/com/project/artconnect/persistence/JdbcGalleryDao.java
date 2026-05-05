package com.project.artconnect.persistence;

import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcGalleryDao implements GalleryDao {

    @Override
    public Optional<Gallery> findById(Long id) {
        String sql = "SELECT id, name, address, owner_name, opening_hours, contact_phone, rating, website FROM galleries WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding gallery by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Gallery> findAll() {
        List<Gallery> galleries = new ArrayList<>();
        String sql = "SELECT id, name, address, owner_name, opening_hours, contact_phone, rating, website FROM galleries ORDER BY name";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                galleries.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all galleries", e);
        }
        return galleries;
    }

    private Gallery mapRow(ResultSet rs) throws SQLException {
        Gallery gallery = new Gallery();
        gallery.setName(rs.getString("name"));
        gallery.setAddress(rs.getString("address"));
        gallery.setOwnerName(rs.getString("owner_name"));
        gallery.setOpeningHours(rs.getString("opening_hours"));
        gallery.setContactPhone(rs.getString("contact_phone"));
        gallery.setRating(rs.getDouble("rating"));
        gallery.setWebsite(rs.getString("website"));
        return gallery;
    }
}
