package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcArtistDao implements ArtistDao {

    @Override
    public List<Artist> findAll() {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT id, name, bio, birth_year, contact_email, phone, city, website, social_media, is_active FROM artists ORDER BY name";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                long id = rs.getLong("id");
                Artist artist = mapRow(rs);
                artist.setDisciplines(fetchDisciplinesForArtist(id));
                artists.add(artist);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all artists", e);
        }
        return artists;
    }

    @Override
    public void save(Artist artist) {
        String insertArtist = "INSERT INTO artists (name, bio, birth_year, contact_email, phone, city, website, social_media, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long artistId;
                try (PreparedStatement ps = conn.prepareStatement(insertArtist, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, artist.getName());
                    ps.setString(2, artist.getBio());
                    setNullableInt(ps, 3, artist.getBirthYear());
                    ps.setString(4, artist.getContactEmail());
                    ps.setString(5, artist.getPhone());
                    ps.setString(6, artist.getCity());
                    ps.setString(7, artist.getWebsite());
                    ps.setString(8, artist.getSocialMedia());
                    ps.setBoolean(9, artist.isActive());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No generated key for artist");
                        artistId = keys.getLong(1);
                    }
                }
                saveDisciplines(conn, artistId, artist.getDisciplines());
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving artist: " + artist.getName(), e);
        }
    }

    @Override
    public void update(Artist artist) {
        String updateSql = "UPDATE artists SET bio=?, birth_year=?, contact_email=?, phone=?, city=?, website=?, social_media=?, is_active=? WHERE name=?";
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, artist.getBio());
                    setNullableInt(ps, 2, artist.getBirthYear());
                    ps.setString(3, artist.getContactEmail());
                    ps.setString(4, artist.getPhone());
                    ps.setString(5, artist.getCity());
                    ps.setString(6, artist.getWebsite());
                    ps.setString(7, artist.getSocialMedia());
                    ps.setBoolean(8, artist.isActive());
                    ps.setString(9, artist.getName());
                    ps.executeUpdate();
                }
                Long artistId = findArtistIdByName(conn, artist.getName());
                if (artistId != null) {
                    try (PreparedStatement del = conn.prepareStatement("DELETE FROM artist_disciplines WHERE artist_id = ?")) {
                        del.setLong(1, artistId);
                        del.executeUpdate();
                    }
                    saveDisciplines(conn, artistId, artist.getDisciplines());
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating artist: " + artist.getName(), e);
        }
    }

    @Override
    public void delete(String artistName) {
        String sql = "DELETE FROM artists WHERE name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, artistName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting artist: " + artistName, e);
        }
    }

    @Override
    public List<Artist> findByCity(String city) {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT id, name, bio, birth_year, contact_email, phone, city, website, social_media, is_active FROM artists WHERE city = ? ORDER BY name";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, city);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    Artist artist = mapRow(rs);
                    artist.setDisciplines(fetchDisciplinesForArtist(id));
                    artists.add(artist);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding artists by city: " + city, e);
        }
        return artists;
    }

    private Artist mapRow(ResultSet rs) throws SQLException {
        Artist artist = new Artist();
        artist.setName(rs.getString("name"));
        artist.setBio(rs.getString("bio"));
        int birthYear = rs.getInt("birth_year");
        if (!rs.wasNull()) artist.setBirthYear(birthYear);
        artist.setContactEmail(rs.getString("contact_email"));
        artist.setPhone(rs.getString("phone"));
        artist.setCity(rs.getString("city"));
        artist.setWebsite(rs.getString("website"));
        artist.setSocialMedia(rs.getString("social_media"));
        artist.setActive(rs.getBoolean("is_active"));
        return artist;
    }

    private List<Discipline> fetchDisciplinesForArtist(long artistId) {
        List<Discipline> disciplines = new ArrayList<>();
        String sql = "SELECT d.name FROM disciplines d JOIN artist_disciplines ad ON d.id = ad.discipline_id WHERE ad.artist_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, artistId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    disciplines.add(new Discipline(rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching disciplines for artist id " + artistId, e);
        }
        return disciplines;
    }

    private void saveDisciplines(Connection conn, long artistId, List<Discipline> disciplines) throws SQLException {
        if (disciplines == null || disciplines.isEmpty()) return;
        String insertLink = "INSERT IGNORE INTO artist_disciplines (artist_id, discipline_id) VALUES (?, ?)";
        for (Discipline d : disciplines) {
            long disciplineId = getOrCreateDiscipline(conn, d.getName());
            try (PreparedStatement ps = conn.prepareStatement(insertLink)) {
                ps.setLong(1, artistId);
                ps.setLong(2, disciplineId);
                ps.executeUpdate();
            }
        }
    }

    private long getOrCreateDiscipline(Connection conn, String name) throws SQLException {
        String select = "SELECT id FROM disciplines WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        String insert = "INSERT INTO disciplines (name) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Could not get or create discipline: " + name);
    }

    Long findArtistIdByName(Connection conn, String name) throws SQLException {
        String sql = "SELECT id FROM artists WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        return null;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value != null) ps.setInt(index, value);
        else ps.setNull(index, Types.INTEGER);
    }
}
