package com.project.artconnect.util;

import com.project.artconnect.persistence.*;
import com.project.artconnect.service.*;
import com.project.artconnect.service.impl.*;

/**
 * Service Provider wiring JDBC-backed services.
 * Update DatabaseConfig credentials before running.
 */
public class ServiceProvider {

    private static final ArtistService artistService;
    private static final ArtworkService artworkService;
    private static final GalleryService galleryService;
    private static final WorkshopService workshopService;
    private static final CommunityService communityService;

    private ServiceProvider() {}

    static {
        JdbcArtistDao artistDao = new JdbcArtistDao();
        JdbcArtworkDao artworkDao = new JdbcArtworkDao();
        JdbcGalleryDao galleryDao = new JdbcGalleryDao();
        JdbcExhibitionDao exhibitionDao = new JdbcExhibitionDao();
        JdbcWorkshopDao workshopDao = new JdbcWorkshopDao();
        JdbcCommunityMemberDao memberDao = new JdbcCommunityMemberDao();

        artistService = new JdbcArtistService(artistDao);
        artworkService = new JdbcArtworkService(artworkDao);
        galleryService = new JdbcGalleryService(galleryDao, exhibitionDao);
        workshopService = new JdbcWorkshopService(workshopDao);
        communityService = new JdbcCommunityService(memberDao);
    }

    public static ArtistService getArtistService() {
        return artistService;
    }

    public static ArtworkService getArtworkService() {
        return artworkService;
    }

    public static GalleryService getGalleryService() {
        return galleryService;
    }

    public static WorkshopService getWorkshopService() {
        return workshopService;
    }

    public static CommunityService getCommunityService() {
        return communityService;
    }
}
