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
