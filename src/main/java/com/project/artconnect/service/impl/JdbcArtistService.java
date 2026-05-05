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
            while (rs.next()) {
                disciplines.add(new Discipline(rs.getString("name")));
            }
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
