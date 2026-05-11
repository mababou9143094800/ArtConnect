# Étape 4 — Intégration de la base de données dans ArtConnect

---

## 1. Code des entités

### Artist.java
```java
package com.project.artconnect.model;

import java.util.ArrayList;
import java.util.List;

public class Artist {
    private String name;
    private String bio;
    private Integer birthYear;
    private List<Discipline> disciplines = new ArrayList<>();
    private String contactEmail;
    private String phone;
    private String city;
    private String website;
    private String socialMedia;
    private boolean isActive;
    private List<Artwork> artworks = new ArrayList<>();

    public Artist() {}

    public Artist(String name, String bio, Integer birthYear, String contactEmail, String city) {
        this.name = name;
        this.bio = bio;
        this.birthYear = birthYear;
        this.contactEmail = contactEmail;
        this.city = city;
        this.isActive = true;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }
    public List<Discipline> getDisciplines() { return disciplines; }
    public void setDisciplines(List<Discipline> disciplines) { this.disciplines = disciplines; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getSocialMedia() { return socialMedia; }
    public void setSocialMedia(String socialMedia) { this.socialMedia = socialMedia; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public List<Artwork> getArtworks() { return artworks; }
    public void setArtworks(List<Artwork> artworks) { this.artworks = artworks; }

    public void addArtwork(Artwork artwork) {
        this.artworks.add(artwork);
        if (artwork.getArtist() != this) artwork.setArtist(this);
    }

    @Override
    public String toString() { return name; }
}
```

### Artwork.java
```java
package com.project.artconnect.model;

import java.util.ArrayList;
import java.util.List;

public class Artwork {
    private String title;
    private Integer creationYear;
    private String type;
    private String medium;
    private String dimensions;
    private String description;
    private double price;
    private Status status;
    private Artist artist;
    private List<ArtworkTag> tags = new ArrayList<>();

    public enum Status { FOR_SALE, SOLD, EXHIBITED }

    public Artwork() {}

    public Artwork(String title, Integer creationYear, String type, double price, Artist artist) {
        this.title = title;
        this.creationYear = creationYear;
        this.type = type;
        this.price = price;
        this.artist = artist;
        this.status = Status.FOR_SALE;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getCreationYear() { return creationYear; }
    public void setCreationYear(Integer creationYear) { this.creationYear = creationYear; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMedium() { return medium; }
    public void setMedium(String medium) { this.medium = medium; }
    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Artist getArtist() { return artist; }
    public void setArtist(Artist artist) { this.artist = artist; }
    public List<ArtworkTag> getTags() { return tags; }
    public void setTags(List<ArtworkTag> tags) { this.tags = tags; }

    @Override
    public String toString() { return title; }
}
```

### Gallery.java
```java
package com.project.artconnect.model;

import java.util.ArrayList;
import java.util.List;

public class Gallery {
    private String name;
    private String address;
    private String ownerName;
    private String openingHours;
    private String contactPhone;
    private double rating;
    private String website;
    private List<Exhibition> exhibitions = new ArrayList<>();

    public Gallery() {}

    public Gallery(String name, String address, double rating) {
        this.name = name;
        this.address = address;
        this.rating = rating;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public List<Exhibition> getExhibitions() { return exhibitions; }
    public void setExhibitions(List<Exhibition> exhibitions) { this.exhibitions = exhibitions; }

    public void addExhibition(Exhibition exhibition) {
        this.exhibitions.add(exhibition);
        if (exhibition.getGallery() != this) exhibition.setGallery(this);
    }

    @Override
    public String toString() { return name; }
}
```

### Exhibition.java
```java
package com.project.artconnect.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Exhibition {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Gallery gallery;
    private String curatorName;
    private String theme;
    private List<Artwork> artworks = new ArrayList<>();

    public Exhibition() {}

    public Exhibition(String title, LocalDate startDate, LocalDate endDate, Gallery gallery) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.gallery = gallery;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Gallery getGallery() { return gallery; }
    public void setGallery(Gallery gallery) { this.gallery = gallery; }
    public String getCuratorName() { return curatorName; }
    public void setCuratorName(String curatorName) { this.curatorName = curatorName; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public List<Artwork> getArtworks() { return artworks; }
    public void setArtworks(List<Artwork> artworks) { this.artworks = artworks; }

    @Override
    public String toString() { return title; }
}
```

### Workshop.java
```java
package com.project.artconnect.model;

import java.time.LocalDateTime;

public class Workshop {
    private String title;
    private LocalDateTime date;
    private int durationMinutes;
    private int maxParticipants;
    private double price;
    private Artist instructor;
    private String location;
    private String description;
    private String level;

    public Workshop() {}

    public Workshop(String title, LocalDateTime date, Artist instructor, double price) {
        this.title = title;
        this.date = date;
        this.instructor = instructor;
        this.price = price;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public Artist getInstructor() { return instructor; }
    public void setInstructor(Artist instructor) { this.instructor = instructor; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    @Override
    public String toString() { return title; }
}
```

### CommunityMember.java
```java
package com.project.artconnect.model;

import java.util.ArrayList;
import java.util.List;

public class CommunityMember {
    private String name;
    private String email;
    private Integer birthYear;
    private String phone;
    private String city;
    private List<Discipline> favoriteDisciplines = new ArrayList<>();
    private String membershipType;
    private List<Booking> bookings = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    public CommunityMember() {}

    public CommunityMember(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public List<Discipline> getFavoriteDisciplines() { return favoriteDisciplines; }
    public void setFavoriteDisciplines(List<Discipline> favoriteDisciplines) { this.favoriteDisciplines = favoriteDisciplines; }
    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
        if (booking.getMember() != this) booking.setMember(this);
    }

    @Override
    public String toString() { return name; }
}
```

### Booking.java
```java
package com.project.artconnect.model;

import java.time.LocalDateTime;

public class Booking {
    private Workshop workshop;
    private CommunityMember member;
    private LocalDateTime bookingDate;
    private String paymentStatus;

    public Booking() {}

    public Booking(Workshop workshop, CommunityMember member) {
        this.workshop = workshop;
        this.member = member;
        this.bookingDate = LocalDateTime.now();
        this.paymentStatus = "PENDING";
    }

    public Workshop getWorkshop() { return workshop; }
    public void setWorkshop(Workshop workshop) { this.workshop = workshop; }
    public CommunityMember getMember() { return member; }
    public void setMember(CommunityMember member) { this.member = member; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
```

### Review.java
```java
package com.project.artconnect.model;

import java.time.LocalDate;

public class Review {
    private CommunityMember reviewer;
    private Artwork artwork;
    private int rating;
    private String comment;
    private LocalDate reviewDate;

    public Review() {}

    public Review(CommunityMember reviewer, Artwork artwork, int rating, String comment) {
        this.reviewer = reviewer;
        this.artwork = artwork;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = LocalDate.now();
    }

    public CommunityMember getReviewer() { return reviewer; }
    public void setReviewer(CommunityMember reviewer) { this.reviewer = reviewer; }
    public Artwork getArtwork() { return artwork; }
    public void setArtwork(Artwork artwork) { this.artwork = artwork; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }
}
```

### Discipline.java
```java
package com.project.artconnect.model;

public class Discipline {
    private String name;

    public Discipline() {}
    public Discipline(String name) { this.name = name; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
```

### ArtworkTag.java
```java
package com.project.artconnect.model;

public class ArtworkTag {
    private String name;

    public ArtworkTag() {}
    public ArtworkTag(String name) { this.name = name; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
```

---

## 2. Code des DAO

### Interfaces DAO

#### ArtistDao.java
```java
package com.project.artconnect.dao;

import com.project.artconnect.model.Artist;
import java.util.List;

public interface ArtistDao {
    List<Artist> findAll();
    void save(Artist artist);
    void update(Artist artist);
    void delete(String artistName);
    List<Artist> findByCity(String city);
}
```

#### ArtworkDao.java
```java
package com.project.artconnect.dao;

import com.project.artconnect.model.Artwork;
import java.util.List;

public interface ArtworkDao {
    List<Artwork> findAll();
    void save(Artwork artwork);
    void update(Artwork artwork);
    void delete(String title);
    List<Artwork> findByArtistName(String artistName);
}
```

#### GalleryDao.java
```java
package com.project.artconnect.dao;

import com.project.artconnect.model.Gallery;
import java.util.List;
import java.util.Optional;

public interface GalleryDao {
    Optional<Gallery> findById(Long id);
    List<Gallery> findAll();
}
```

#### ExhibitionDao.java
```java
package com.project.artconnect.dao;

import com.project.artconnect.model.Exhibition;
import java.util.List;

public interface ExhibitionDao {
    List<Exhibition> findAll();
    void save(Exhibition exhibition);
    void update(Exhibition exhibition);
    void delete(String title);
}
```

#### WorkshopDao.java
```java
package com.project.artconnect.dao;

import com.project.artconnect.model.Workshop;
import java.util.List;
import java.util.Optional;

public interface WorkshopDao {
    Optional<Workshop> findById(Long id);
    List<Workshop> findAll();
}
```

#### CommunityMemberDao.java
```java
package com.project.artconnect.dao;

import com.project.artconnect.model.CommunityMember;
import java.util.List;
import java.util.Optional;

public interface CommunityMemberDao {
    Optional<CommunityMember> findById(Long id);
    List<CommunityMember> findAll();
}
```

---

### Implémentations JDBC

#### JdbcArtistDao.java
```java
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
                while (rs.next()) disciplines.add(new Discipline(rs.getString("name")));
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
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM disciplines WHERE name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO disciplines (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Could not get or create discipline: " + name);
    }

    Long findArtistIdByName(Connection conn, String name) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM artists WHERE name = ?")) {
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
```

#### JdbcArtworkDao.java
```java
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
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM artworks WHERE title = ?")) {
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
                while (rs.next()) tags.add(new ArtworkTag(rs.getString("name")));
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
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM artwork_tags WHERE name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO artwork_tags (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Could not get or create tag: " + name);
    }

    Long findArtworkIdByTitle(Connection conn, String title) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM artworks WHERE title = ?")) {
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
```

#### JdbcGalleryDao.java
```java
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
            while (rs.next()) galleries.add(mapRow(rs));
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
```

#### JdbcExhibitionDao.java
```java
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
            while (rs.next()) exhibitions.add(mapRow(rs));
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
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM exhibitions WHERE title = ?")) {
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
```

#### JdbcWorkshopDao.java
```java
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
            while (rs.next()) workshops.add(mapRow(rs));
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
```

#### JdbcCommunityMemberDao.java
```java
package com.project.artconnect.persistence;

import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCommunityMemberDao implements CommunityMemberDao {

    @Override
    public Optional<CommunityMember> findById(Long id) {
        String sql = "SELECT id, name, email, birth_year, phone, city, membership_type FROM community_members WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CommunityMember member = mapRow(rs);
                    member.setFavoriteDisciplines(fetchDisciplinesForMember(rs.getLong("id")));
                    return Optional.of(member);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding member by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<CommunityMember> findAll() {
        List<CommunityMember> members = new ArrayList<>();
        String sql = "SELECT id, name, email, birth_year, phone, city, membership_type FROM community_members ORDER BY name";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                long id = rs.getLong("id");
                CommunityMember member = mapRow(rs);
                member.setFavoriteDisciplines(fetchDisciplinesForMember(id));
                members.add(member);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all community members", e);
        }
        return members;
    }

    private CommunityMember mapRow(ResultSet rs) throws SQLException {
        CommunityMember member = new CommunityMember();
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        int birthYear = rs.getInt("birth_year");
        if (!rs.wasNull()) member.setBirthYear(birthYear);
        member.setPhone(rs.getString("phone"));
        member.setCity(rs.getString("city"));
        member.setMembershipType(rs.getString("membership_type"));
        return member;
    }

    private List<Discipline> fetchDisciplinesForMember(long memberId) {
        List<Discipline> disciplines = new ArrayList<>();
        String sql = "SELECT d.name FROM disciplines d JOIN member_favorite_disciplines mfd ON d.id = mfd.discipline_id WHERE mfd.member_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) disciplines.add(new Discipline(rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching disciplines for member id " + memberId, e);
        }
        return disciplines;
    }
}
```

---

## 3. Code des services

### Interfaces de service

#### ArtistService.java
```java
package com.project.artconnect.service;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import java.util.List;
import java.util.Optional;

public interface ArtistService {
    List<Artist> getAllArtists();
    Optional<Artist> getArtistByName(String name);
    void createArtist(Artist artist);
    void updateArtist(Artist artist);
    void deleteArtist(String name);
    List<Discipline> getAllDisciplines();
    List<Artist> searchArtists(String query, String disciplineName, String city);
}
```

#### ArtworkService.java
```java
package com.project.artconnect.service;

import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.Artist;
import java.util.List;
import java.util.Optional;

public interface ArtworkService {
    List<Artwork> getAllArtworks();
    Optional<Artwork> getArtworkByTitle(String title);
    List<Artwork> getArtworksByArtist(Artist artist);
    void createArtwork(Artwork artwork);
    void updateArtwork(Artwork artwork);
    void deleteArtwork(String title);
}
```

#### GalleryService.java
```java
package com.project.artconnect.service;

import com.project.artconnect.model.Gallery;
import com.project.artconnect.model.Exhibition;
import java.util.List;
import java.util.Optional;

public interface GalleryService {
    List<Gallery> getAllGalleries();
    Optional<Gallery> getGalleryByName(String name);
    List<Exhibition> getExhibitionsByGallery(Gallery gallery);
}
```

#### WorkshopService.java
```java
package com.project.artconnect.service;

import com.project.artconnect.model.Workshop;
import com.project.artconnect.model.Booking;
import com.project.artconnect.model.CommunityMember;
import java.util.List;
import java.util.Optional;

public interface WorkshopService {
    List<Workshop> getAllWorkshops();
    Optional<Workshop> getWorkshopByTitle(String title);
    void bookWorkshop(Workshop workshop, CommunityMember member);
    List<Booking> getBookingsByMember(CommunityMember member);
}
```

#### CommunityService.java
```java
package com.project.artconnect.service;

import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Review;
import java.util.List;
import java.util.Optional;

public interface CommunityService {
    List<CommunityMember> getAllMembers();
    Optional<CommunityMember> getMemberByName(String name);
    List<Review> getReviewsByMember(CommunityMember member);
}
```

---

### Implémentations JDBC des services

#### JdbcArtistService.java
```java
package com.project.artconnect.service.impl;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JdbcArtistService implements ArtistService {

    private final ArtistDao artistDao;

    public JdbcArtistService(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }

    @Override
    public List<Artist> getAllArtists() {
        return artistDao.findAll();
    }

    @Override
    public Optional<Artist> getArtistByName(String name) {
        return artistDao.findAll().stream()
                .filter(a -> a.getName().equals(name))
                .findFirst();
    }

    @Override
    public void createArtist(Artist artist) {
        artistDao.save(artist);
    }

    @Override
    public void updateArtist(Artist artist) {
        artistDao.update(artist);
    }

    @Override
    public void deleteArtist(String name) {
        artistDao.delete(name);
    }

    @Override
    public List<Discipline> getAllDisciplines() {
        List<Discipline> disciplines = new ArrayList<>();
        String sql = "SELECT name FROM disciplines ORDER BY name";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) disciplines.add(new Discipline(rs.getString("name")));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching disciplines", e);
        }
        return disciplines;
    }

    @Override
    public List<Artist> searchArtists(String query, String disciplineName, String city) {
        return artistDao.findAll().stream()
                .filter(a -> query == null || query.isEmpty()
                        || a.getName().toLowerCase().contains(query.toLowerCase()))
                .filter(a -> city == null || city.isEmpty()
                        || (a.getCity() != null && a.getCity().equalsIgnoreCase(city)))
                .filter(a -> disciplineName == null || disciplineName.isEmpty()
                        || a.getDisciplines().stream().anyMatch(d -> d.getName().equals(disciplineName)))
                .collect(Collectors.toList());
    }
}
```

#### JdbcArtworkService.java
```java
package com.project.artconnect.service.impl;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.service.ArtworkService;

import java.util.List;
import java.util.Optional;

public class JdbcArtworkService implements ArtworkService {

    private final ArtworkDao artworkDao;

    public JdbcArtworkService(ArtworkDao artworkDao) {
        this.artworkDao = artworkDao;
    }

    @Override
    public List<Artwork> getAllArtworks() {
        return artworkDao.findAll();
    }

    @Override
    public Optional<Artwork> getArtworkByTitle(String title) {
        return artworkDao.findAll().stream()
                .filter(aw -> aw.getTitle().equals(title))
                .findFirst();
    }

    @Override
    public List<Artwork> getArtworksByArtist(Artist artist) {
        return artworkDao.findByArtistName(artist.getName());
    }

    @Override
    public void createArtwork(Artwork artwork) {
        artworkDao.save(artwork);
    }

    @Override
    public void updateArtwork(Artwork artwork) {
        artworkDao.update(artwork);
    }

    @Override
    public void deleteArtwork(String title) {
        artworkDao.delete(title);
    }
}
```

#### JdbcGalleryService.java
```java
package com.project.artconnect.service.impl;

import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.service.GalleryService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JdbcGalleryService implements GalleryService {

    private final GalleryDao galleryDao;
    private final ExhibitionDao exhibitionDao;

    public JdbcGalleryService(GalleryDao galleryDao, ExhibitionDao exhibitionDao) {
        this.galleryDao = galleryDao;
        this.exhibitionDao = exhibitionDao;
    }

    @Override
    public List<Gallery> getAllGalleries() {
        List<Gallery> galleries = galleryDao.findAll();
        List<Exhibition> allExhibitions = exhibitionDao.findAll();
        for (Gallery gallery : galleries) {
            List<Exhibition> galleryExhibitions = allExhibitions.stream()
                    .filter(e -> e.getGallery() != null && e.getGallery().getName().equals(gallery.getName()))
                    .collect(Collectors.toList());
            gallery.setExhibitions(galleryExhibitions);
        }
        return galleries;
    }

    @Override
    public Optional<Gallery> getGalleryByName(String name) {
        return getAllGalleries().stream()
                .filter(g -> g.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Exhibition> getExhibitionsByGallery(Gallery gallery) {
        return exhibitionDao.findAll().stream()
                .filter(e -> e.getGallery() != null && e.getGallery().getName().equals(gallery.getName()))
                .collect(Collectors.toList());
    }
}
```

#### JdbcWorkshopService.java
```java
package com.project.artconnect.service.impl;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Booking;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcWorkshopService implements WorkshopService {

    private final WorkshopDao workshopDao;

    public JdbcWorkshopService(WorkshopDao workshopDao) {
        this.workshopDao = workshopDao;
    }

    @Override
    public List<Workshop> getAllWorkshops() {
        return workshopDao.findAll();
    }

    @Override
    public Optional<Workshop> getWorkshopByTitle(String title) {
        return workshopDao.findAll().stream()
                .filter(w -> w.getTitle().equals(title))
                .findFirst();
    }

    @Override
    public void bookWorkshop(Workshop workshop, CommunityMember member) {
        String getWorkshopId = "SELECT id FROM workshops WHERE title = ?";
        String getMemberId = "SELECT id FROM community_members WHERE name = ?";
        String insertBooking = "INSERT INTO bookings (workshop_id, member_id, payment_status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long workshopId = requireId(conn, getWorkshopId, workshop.getTitle(), "Workshop");
                long memberId = requireId(conn, getMemberId, member.getName(), "Member");
                try (PreparedStatement ps = conn.prepareStatement(insertBooking)) {
                    ps.setLong(1, workshopId);
                    ps.setLong(2, memberId);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error booking workshop '" + workshop.getTitle() + "' for " + member.getName(), e);
        }
    }

    @Override
    public List<Booking> getBookingsByMember(CommunityMember member) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_date, b.payment_status, "
                + "w.title AS workshop_title, w.workshop_date, w.duration_minutes, w.max_participants, "
                + "w.price AS workshop_price, w.location, w.description, w.level, "
                + "a.name AS instructor_name "
                + "FROM bookings b "
                + "JOIN workshops w ON b.workshop_id = w.id "
                + "JOIN community_members cm ON b.member_id = cm.id "
                + "JOIN artists a ON w.instructor_artist_id = a.id "
                + "WHERE cm.name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Workshop workshop = new Workshop();
                    workshop.setTitle(rs.getString("workshop_title"));
                    Timestamp ts = rs.getTimestamp("workshop_date");
                    if (ts != null) workshop.setDate(ts.toLocalDateTime());
                    workshop.setDurationMinutes(rs.getInt("duration_minutes"));
                    workshop.setMaxParticipants(rs.getInt("max_participants"));
                    workshop.setPrice(rs.getDouble("workshop_price"));
                    workshop.setLocation(rs.getString("location"));
                    workshop.setDescription(rs.getString("description"));
                    workshop.setLevel(rs.getString("level"));
                    Artist instructor = new Artist();
                    instructor.setName(rs.getString("instructor_name"));
                    workshop.setInstructor(instructor);

                    Booking booking = new Booking();
                    booking.setWorkshop(workshop);
                    booking.setMember(member);
                    Timestamp bookingTs = rs.getTimestamp("booking_date");
                    if (bookingTs != null) booking.setBookingDate(bookingTs.toLocalDateTime());
                    booking.setPaymentStatus(rs.getString("payment_status"));
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bookings for member: " + member.getName(), e);
        }
        return bookings;
    }

    private long requireId(Connection conn, String sql, String name, String entityType) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        throw new SQLException(entityType + " not found: " + name);
    }
}
```

#### JdbcCommunityService.java
```java
package com.project.artconnect.service.impl;

import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Review;
import com.project.artconnect.service.CommunityService;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCommunityService implements CommunityService {

    private final CommunityMemberDao memberDao;

    public JdbcCommunityService(CommunityMemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public List<CommunityMember> getAllMembers() {
        return memberDao.findAll();
    }

    @Override
    public Optional<CommunityMember> getMemberByName(String name) {
        return memberDao.findAll().stream()
                .filter(m -> m.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Review> getReviewsByMember(CommunityMember member) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.rating, r.comment, r.review_date, aw.title AS artwork_title "
                + "FROM reviews r "
                + "JOIN community_members cm ON r.reviewer_member_id = cm.id "
                + "JOIN artworks aw ON r.artwork_id = aw.id "
                + "WHERE cm.name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review();
                    review.setReviewer(member);
                    Artwork artwork = new Artwork();
                    artwork.setTitle(rs.getString("artwork_title"));
                    review.setArtwork(artwork);
                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    Date reviewDate = rs.getDate("review_date");
                    if (reviewDate != null) review.setReviewDate(reviewDate.toLocalDate());
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reviews for member: " + member.getName(), e);
        }
        return reviews;
    }
}
```

---

## 4. Description de l'architecture retenue

### Schéma de couches

```
┌──────────────────────────────────────────┐
│           Couche UI (JavaFX)             │
│  ArtistController, ArtworkController,   │
│  GalleryController, WorkshopController…  │
│           (non modifiée)                 │
└──────────────────┬───────────────────────┘
                   │ appelle
┌──────────────────▼───────────────────────┐
│           Couche Service                 │
│  «interfaces» ArtistService,             │
│   ArtworkService, GalleryService…        │
│  «impl JDBC» JdbcArtistService,          │
│   JdbcArtworkService…                    │
└──────────────────┬───────────────────────┘
                   │ utilise
┌──────────────────▼───────────────────────┐
│             Couche DAO                   │
│  «interfaces» ArtistDao, ArtworkDao…     │
│  «impl JDBC» JdbcArtistDao,              │
│   JdbcArtworkDao, JdbcGalleryDao…        │
└──────────────────┬───────────────────────┘
                   │ PreparedStatement
┌──────────────────▼───────────────────────┐
│     ConnectionManager / DatabaseConfig   │
│   jdbc:mysql://localhost:3306/artconnect │
└──────────────────┬───────────────────────┘
                   │
          ┌────────▼────────┐
          │  MySQL artconnect│
          └─────────────────┘
```

### Câblage dans ServiceProvider

```java
JdbcArtistDao artistDao       = new JdbcArtistDao();
JdbcArtworkDao artworkDao     = new JdbcArtworkDao();
JdbcGalleryDao galleryDao     = new JdbcGalleryDao();
JdbcExhibitionDao exhibitionDao = new JdbcExhibitionDao();
JdbcWorkshopDao workshopDao   = new JdbcWorkshopDao();
JdbcCommunityMemberDao memberDao = new JdbcCommunityMemberDao();

artistService    = new JdbcArtistService(artistDao);
artworkService   = new JdbcArtworkService(artworkDao);
galleryService   = new JdbcGalleryService(galleryDao, exhibitionDao);
workshopService  = new JdbcWorkshopService(workshopDao);
communityService = new JdbcCommunityService(memberDao);
```

---

## 5. Captures d'écran de l'application en fonctionnement

> **À compléter** : lancer l'application avec `mvn javafx:run` (MySQL démarré, base chargée),
> puis ajouter les captures ci-dessous.

### Liste des artistes chargée depuis la base

*(capture d'écran)*

### Ajout d'un artiste — persistance vérifiée en base

*(capture d'écran)*

### Liste des œuvres avec artiste associé

*(capture d'écran)*

### Galeries et expositions

*(capture d'écran)*

### Ateliers et réservation

*(capture d'écran)*

---

*ArtConnect Pro — EFREI INGE1 — TI603*
