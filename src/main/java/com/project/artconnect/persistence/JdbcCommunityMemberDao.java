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
                while (rs.next()) {
                    disciplines.add(new Discipline(rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching disciplines for member id " + memberId, e);
        }
        return disciplines;
    }
}
