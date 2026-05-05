package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.ArtworkTag;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcArtworkDao implements ArtworkDao {

    @Override
    public List<Artwork> findAll() {
        List<Artwork> artworks = new ArrayList<>();
        String sql = "SELECT aw.id, aw.title, aw.creation_year, aw.type, aw.medium, aw.dimensions, "
                + "aw.description, aw.price, aw.status, ar.name AS artist_name "
                + "FROM artworks aw JOIN artists ar ON aw.artist_id = ar.id ORDER BY aw.title";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                long id = rs.getLong("id");
                Artwork artwork = mapRow(rs);
                artwork.setTags(fetchTagsForArtwork(id));
                artworks.add(artwork);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all artworks", e);
        }
        return artworks;
    }

    @Override
    public void save(Artwork artwork) {
        String getArtistId = "SELECT id FROM artists WHERE name = ?";
        String insertArtwork = "INSERT INTO artworks (artist_id, title, creation_year, type, medium, dimensions, description, price, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long artistId = requireArtistId(conn, getArtistId, artwork.getArtist().getName());
                long artworkId;
                try (PreparedStatement ps = conn.prepareStatement(insertArtwork, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, artistId);
                    ps.setString(2, artwork.getTitle());
                    setNullableInt(ps, 3, artwork.getCreationYear());
                    ps.setString(4, artwork.getType());
                    ps.setString(5, artwork.getMedium());
                    ps.setString(6, artwork.getDimensions());
                    ps.setString(7, artwork.getDescription());
                    ps.setDouble(8, artwork.getPrice());
                    ps.setString(9, artwork.getStatus() != null ? artwork.getStatus().name() : "FOR_SALE");
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No generated key for artwork");
                        artworkId = keys.getLong(1);
                    }
                }
                saveTags(conn, artworkId, artwork.getTags());
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving artwork: " + artwork.getTitle(), e);
        }
    }

    @Override
    public void update(Artwork artwork) {
        String updateSql = "UPDATE artworks SET creation_year=?, type=?, medium=?, dimensions=?, description=?, price=?, status=? WHERE title=?";
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    setNullableInt(ps, 1, artwork.getCreationYear());
                    ps.setString(2, artwork.getType());
                    ps.setString(3, artwork.getMedium());
                    ps.setString(4, artwork.getDimensions());
                    ps.setString(5, artwork.getDescription());
                    ps.setDouble(6, artwork.getPrice());
                    ps.setString(7, artwork.getStatus() != null ? artwork.getStatus().name() : "FOR_SALE");
                    ps.setString(8, artwork.getTitle());
                    ps.executeUpdate();
                }
                Long artworkId = findArtworkIdByTitle(conn, artwork.getTitle());
                if (artworkId != null) {
                    try (PreparedStatement del = conn.prepareStatement("DELETE FROM artwork_tag_links WHERE artwork_id = ?")) {
                        del.setLong(1, artworkId);
                        del.executeUpdate();
                    }
                    saveTags(conn, artworkId, artwork.getTags());
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating artwork: " + artwork.getTitle(), e);
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM artworks WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting artwork: " + title, e);
        }
    }

    @Override
    public List<Artwork> findByArtistName(String artistName) {
        List<Artwork> artworks = new ArrayList<>();
        String sql = "SELECT aw.id, aw.title, aw.creation_year, aw.type, aw.medium, aw.dimensions, "
                + "aw.description, aw.price, aw.status, ar.name AS artist_name "
                + "FROM artworks aw JOIN artists ar ON aw.artist_id = ar.id WHERE ar.name = ? ORDER BY aw.title";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, artistName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    Artwork artwork = mapRow(rs);
                    artwork.setTags(fetchTagsForArtwork(id));
                    artworks.add(artwork);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding artworks by artist: " + artistName, e);
        }
        return artworks;
    }

    private Artwork mapRow(ResultSet rs) throws SQLException {
        Artwork artwork = new Artwork();
        artwork.setTitle(rs.getString("title"));
        int creationYear = rs.getInt("creation_year");
        if (!rs.wasNull()) artwork.setCreationYear(creationYear);
        artwork.setType(rs.getString("type"));
        artwork.setMedium(rs.getString("medium"));
        artwork.setDimensions(rs.getString("dimensions"));
        artwork.setDescription(rs.getString("description"));
        artwork.setPrice(rs.getDouble("price"));
        String statusStr = rs.getString("status");
        if (statusStr != null) artwork.setStatus(Artwork.Status.valueOf(statusStr));
        Artist artist = new Artist();
        artist.setName(rs.getString("artist_name"));
        artwork.setArtist(artist);
        return artwork;
    }

    private List<ArtworkTag> fetchTagsForArtwork(long artworkId) {
        List<ArtworkTag> tags = new ArrayList<>();
        String sql = "SELECT t.name FROM artwork_tags t JOIN artwork_tag_links atl ON t.id = atl.tag_id WHERE atl.artwork_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, artworkId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tags.add(new ArtworkTag(rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching tags for artwork id " + artworkId, e);
        }
        return tags;
    }

    private void saveTags(Connection conn, long artworkId, List<ArtworkTag> tags) throws SQLException {
        if (tags == null || tags.isEmpty()) return;
        String insertLink = "INSERT IGNORE INTO artwork_tag_links (artwork_id, tag_id) VALUES (?, ?)";
        for (ArtworkTag tag : tags) {
            long tagId = getOrCreateTag(conn, tag.getName());
            try (PreparedStatement ps = conn.prepareStatement(insertLink)) {
                ps.setLong(1, artworkId);
                ps.setLong(2, tagId);
                ps.executeUpdate();
            }
        }
    }

    private long getOrCreateTag(Connection conn, String name) throws SQLException {
        String select = "SELECT id FROM artwork_tags WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        String insert = "INSERT INTO artwork_tags (name) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Could not get or create tag: " + name);
    }

    Long findArtworkIdByTitle(Connection conn, String title) throws SQLException {
        String sql = "SELECT id FROM artworks WHERE title = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        return null;
    }

    private long requireArtistId(Connection conn, String sql, String artistName) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, artistName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        throw new SQLException("Artist not found: " + artistName);
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value != null) ps.setInt(index, value);
        else ps.setNull(index, Types.INTEGER);
    }
}
