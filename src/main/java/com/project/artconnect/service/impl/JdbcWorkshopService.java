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
