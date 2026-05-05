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
