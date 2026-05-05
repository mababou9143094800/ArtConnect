DROP DATABASE IF EXISTS artconnect;
CREATE DATABASE artconnect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE artconnect;

CREATE TABLE disciplines (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE artwork_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE artists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    bio TEXT,
    birth_year INT,
    contact_email VARCHAR(150),
    phone VARCHAR(30),
    city VARCHAR(100),
    website VARCHAR(255),
    social_media VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_artist_name UNIQUE (name),
    CONSTRAINT chk_artist_birth_year CHECK (birth_year IS NULL OR birth_year BETWEEN 1900 AND 2026),
    CONSTRAINT chk_artist_email CHECK (contact_email IS NULL OR contact_email LIKE '%@%')
) ENGINE=InnoDB;

CREATE TABLE artist_disciplines (
    artist_id BIGINT NOT NULL,
    discipline_id BIGINT NOT NULL,
    PRIMARY KEY (artist_id, discipline_id),
    CONSTRAINT fk_artist_disc_artist
        FOREIGN KEY (artist_id) REFERENCES artists(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_artist_disc_discipline
        FOREIGN KEY (discipline_id) REFERENCES disciplines(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE galleries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(255),
    owner_name VARCHAR(150),
    opening_hours VARCHAR(120),
    contact_phone VARCHAR(30),
    rating DECIMAL(3,2) DEFAULT 0.00,
    website VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_gallery_name UNIQUE (name),
    CONSTRAINT chk_gallery_rating CHECK (rating BETWEEN 0 AND 5)
) ENGINE=InnoDB;

CREATE TABLE artworks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    artist_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    creation_year INT,
    type VARCHAR(100),
    medium VARCHAR(100),
    dimensions VARCHAR(120),
    description TEXT,
    price DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    status ENUM('FOR_SALE', 'SOLD', 'EXHIBITED') NOT NULL DEFAULT 'FOR_SALE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_artworks_artist
        FOREIGN KEY (artist_id) REFERENCES artists(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_artwork_title_per_artist UNIQUE (artist_id, title),
    CONSTRAINT chk_artwork_price CHECK (price >= 0),
    CONSTRAINT chk_artwork_creation_year CHECK (creation_year IS NULL OR creation_year BETWEEN 1900 AND 2026)
) ENGINE=InnoDB;

CREATE TABLE artwork_tag_links (
    artwork_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (artwork_id, tag_id),
    CONSTRAINT fk_artwork_tag_links_artwork
        FOREIGN KEY (artwork_id) REFERENCES artworks(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_artwork_tag_links_tag
        FOREIGN KEY (tag_id) REFERENCES artwork_tags(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE exhibitions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    gallery_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    description TEXT,
    curator_name VARCHAR(150),
    theme VARCHAR(150),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exhibitions_gallery
        FOREIGN KEY (gallery_id) REFERENCES galleries(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_exhibition_title_per_gallery UNIQUE (gallery_id, title),
    CONSTRAINT chk_exhibition_dates CHECK (end_date >= start_date)
) ENGINE=InnoDB;

CREATE TABLE exhibition_artworks (
    exhibition_id BIGINT NOT NULL,
    artwork_id BIGINT NOT NULL,
    PRIMARY KEY (exhibition_id, artwork_id),
    CONSTRAINT fk_exhibition_artworks_exhibition
        FOREIGN KEY (exhibition_id) REFERENCES exhibitions(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_exhibition_artworks_artwork
        FOREIGN KEY (artwork_id) REFERENCES artworks(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE workshops (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    workshop_date DATETIME NOT NULL,
    duration_minutes INT NOT NULL DEFAULT 60,
    max_participants INT NOT NULL,
    current_participants INT NOT NULL DEFAULT 0,
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    instructor_artist_id BIGINT NOT NULL,
    location VARCHAR(200),
    description TEXT,
    level VARCHAR(30),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_workshops_instructor
        FOREIGN KEY (instructor_artist_id) REFERENCES artists(id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_workshop_duration CHECK (duration_minutes > 0),
    CONSTRAINT chk_workshop_capacity CHECK (max_participants > 0),
    CONSTRAINT chk_workshop_current_capacity CHECK (current_participants >= 0 AND current_participants <= max_participants),
    CONSTRAINT chk_workshop_price CHECK (price >= 0)
) ENGINE=InnoDB;

CREATE TABLE community_members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    birth_year INT,
    phone VARCHAR(30),
    city VARCHAR(100),
    membership_type ENUM('free', 'premium') NOT NULL DEFAULT 'free',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_member_email UNIQUE (email),
    CONSTRAINT chk_member_birth_year CHECK (birth_year IS NULL OR birth_year BETWEEN 1900 AND 2026),
    CONSTRAINT chk_member_email CHECK (email LIKE '%@%')
) ENGINE=InnoDB;

CREATE TABLE member_favorite_disciplines (
    member_id BIGINT NOT NULL,
    discipline_id BIGINT NOT NULL,
    PRIMARY KEY (member_id, discipline_id),
    CONSTRAINT fk_member_fav_disc_member
        FOREIGN KEY (member_id) REFERENCES community_members(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_member_fav_disc_discipline
        FOREIGN KEY (discipline_id) REFERENCES disciplines(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    workshop_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    booking_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_status ENUM('PENDING', 'PAID', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_bookings_workshop
        FOREIGN KEY (workshop_id) REFERENCES workshops(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_bookings_member
        FOREIGN KEY (member_id) REFERENCES community_members(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_booking_member_workshop UNIQUE (workshop_id, member_id)
) ENGINE=InnoDB;

CREATE TABLE reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reviewer_member_id BIGINT NOT NULL,
    artwork_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    review_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_reviews_member
        FOREIGN KEY (reviewer_member_id) REFERENCES community_members(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_reviews_artwork
        FOREIGN KEY (artwork_id) REFERENCES artworks(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_review_once_per_member UNIQUE (reviewer_member_id, artwork_id),
    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB;

-- 3) TABLE D'AUDIT

CREATE TABLE audit_log (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_name  VARCHAR(100) NOT NULL,
    action      ENUM('INSERT','UPDATE','DELETE') NOT NULL,
    record_id   BIGINT,
    old_value   TEXT,
    new_value   TEXT,
    changed_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 4) INDEX DE PERFORMANCE

CREATE INDEX idx_artists_city ON artists(city);
CREATE INDEX idx_artworks_artist_id ON artworks(artist_id);
CREATE INDEX idx_artworks_status ON artworks(status);
CREATE INDEX idx_exhibitions_dates ON exhibitions(start_date, end_date);
CREATE INDEX idx_workshops_date ON workshops(workshop_date);
CREATE INDEX idx_bookings_member ON bookings(member_id);
CREATE INDEX idx_reviews_artwork ON reviews(artwork_id);

-- 5) VUES

-- 5a) Vue simplifiée : portfolio artiste avec stats
CREATE VIEW v_artist_portfolio AS
SELECT
    a.id AS artist_id,
    a.name AS artist_name,
    a.city,
    COUNT(aw.id) AS artworks_count,
    COALESCE(SUM(CASE WHEN aw.status = 'FOR_SALE' THEN 1 ELSE 0 END), 0) AS artworks_for_sale,
    COALESCE(AVG(r.rating), 0) AS avg_rating
FROM artists a
LEFT JOIN artworks aw ON aw.artist_id = a.id
LEFT JOIN reviews r ON r.artwork_id = aw.id
GROUP BY a.id, a.name, a.city;

-- 5b) Vue simplifiée : événements à venir (expositions + ateliers)
CREATE VIEW v_upcoming_events AS
SELECT
    'EXHIBITION' AS event_type,
    e.id AS event_id,
    e.title AS event_title,
    CAST(e.start_date AS DATETIME) AS event_start,
    g.name AS venue,
    NULL AS available_slots
FROM exhibitions e
JOIN galleries g ON g.id = e.gallery_id
WHERE e.end_date >= CURDATE()
UNION ALL
SELECT
    'WORKSHOP' AS event_type,
    w.id AS event_id,
    w.title AS event_title,
    w.workshop_date AS event_start,
    w.location AS venue,
    (w.max_participants - w.current_participants) AS available_slots
FROM workshops w
WHERE w.workshop_date >= NOW();

-- 5c) Vue sécurité : œuvres publiques (masquage email/téléphone artiste)
CREATE VIEW v_artworks_public AS
SELECT
    aw.id                       AS artwork_id,
    aw.title,
    aw.type,
    aw.medium,
    aw.creation_year,
    aw.price,
    aw.status,
    a.name                      AS artist_name,
    a.city                      AS artist_city,
    COALESCE(AVG(r.rating), 0)  AS avg_rating,
    COUNT(r.id)                 AS review_count
FROM artworks aw
JOIN artists a  ON a.id = aw.artist_id
LEFT JOIN reviews r ON r.artwork_id = aw.id
GROUP BY aw.id, aw.title, aw.type, aw.medium, aw.creation_year,
         aw.price, aw.status, a.name, a.city;

-- 5d) Vue masquage : membres avec email partiellement masqué
CREATE VIEW v_members_masked AS
SELECT
    id,
    name,
    CONCAT(LEFT(email, 2), '***@', SUBSTRING_INDEX(email,'@',-1)) AS email_masked,
    city,
    membership_type,
    created_at
FROM community_members;

-- 5e) Vue activité membre : requête simplifiée multi-tables
CREATE VIEW v_member_activity AS
SELECT
    cm.id           AS member_id,
    cm.name,
    cm.membership_type,
    COUNT(DISTINCT b.id)  AS total_bookings,
    COUNT(DISTINCT r.id)  AS total_reviews,
    COALESCE(AVG(r.rating), 0) AS avg_given_rating
FROM community_members cm
LEFT JOIN bookings b ON b.member_id = cm.id AND b.payment_status != 'CANCELLED'
LEFT JOIN reviews  r ON r.reviewer_member_id = cm.id
GROUP BY cm.id, cm.name, cm.membership_type;

-- 6) TRIGGERS

DELIMITER $$

-- 6a) Contrôle de dates : l'exposition ne peut pas commencer dans le passé
CREATE TRIGGER trg_exhibitions_before_insert_future_date
BEFORE INSERT ON exhibitions
FOR EACH ROW
BEGIN
    IF NEW.start_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Exhibition start date cannot be in the past';
    END IF;
END$$

-- 6b) Audit : changement de statut d'une œuvre
CREATE TRIGGER trg_artworks_after_update_status
AFTER UPDATE ON artworks
FOR EACH ROW
BEGIN
    IF OLD.status <> NEW.status THEN
        INSERT INTO audit_log (table_name, action, record_id, old_value, new_value)
        VALUES ('artworks', 'UPDATE', NEW.id, OLD.status, NEW.status);
    END IF;
END$$

-- 6c) Audit : suppression d'une réservation
CREATE TRIGGER trg_bookings_after_delete_audit
AFTER DELETE ON bookings
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, action, record_id, old_value, new_value)
    VALUES ('bookings', 'DELETE', OLD.id,
            CONCAT('workshop=', OLD.workshop_id, ',member=', OLD.member_id, ',status=', OLD.payment_status),
            NULL);
END$$

-- 6d) Capacité atelier : refus si complet
CREATE TRIGGER trg_bookings_before_insert_capacity
BEFORE INSERT ON bookings
FOR EACH ROW
BEGIN
    DECLARE v_max INT;
    DECLARE v_current INT;

    SELECT max_participants, current_participants
      INTO v_max, v_current
      FROM workshops
     WHERE id = NEW.workshop_id
     FOR UPDATE;

    IF v_current >= v_max THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Workshop is full';
    END IF;
END$$

-- 6e) Incrémente le compteur de participants
CREATE TRIGGER trg_bookings_after_insert_increment
AFTER INSERT ON bookings
FOR EACH ROW
BEGIN
    UPDATE workshops
       SET current_participants = current_participants + 1
     WHERE id = NEW.workshop_id;
END$$

-- 6f) Décrémente le compteur de participants
CREATE TRIGGER trg_bookings_after_delete_decrement
AFTER DELETE ON bookings
FOR EACH ROW
BEGIN
    UPDATE workshops
       SET current_participants = GREATEST(current_participants - 1, 0)
     WHERE id = OLD.workshop_id;
END$$

-- 6g) Validation de la note (redondant avec CHECK, mais explicite pour le message)
CREATE TRIGGER trg_reviews_before_insert_validate
BEFORE INSERT ON reviews
FOR EACH ROW
BEGIN
    IF NEW.rating < 1 OR NEW.rating > 5 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Rating must be between 1 and 5';
    END IF;
END$$

DELIMITER ;

-- 7) PROCEDURES STOCKEES

DELIMITER $$

-- 7a) Inscription d'un membre à un atelier (avec contrôles)
CREATE PROCEDURE sp_register_booking(
    IN p_workshop_id BIGINT,
    IN p_member_id BIGINT,
    IN p_payment_status VARCHAR(20)
)
BEGIN
    DECLARE v_exists INT DEFAULT 0;

    START TRANSACTION;

    SELECT COUNT(*)
      INTO v_exists
      FROM bookings
     WHERE workshop_id = p_workshop_id
       AND member_id = p_member_id;

    IF v_exists > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Member already registered for this workshop';
    END IF;

    INSERT INTO bookings (workshop_id, member_id, payment_status)
    VALUES (p_workshop_id, p_member_id, UPPER(COALESCE(p_payment_status, 'PENDING')));

    COMMIT;
END$$

-- 7b) Annulation d'une réservation
CREATE PROCEDURE sp_cancel_booking(
    IN p_workshop_id BIGINT,
    IN p_member_id BIGINT
)
BEGIN
    START TRANSACTION;

    UPDATE bookings
       SET payment_status = 'CANCELLED'
     WHERE workshop_id = p_workshop_id
       AND member_id = p_member_id;

    DELETE FROM bookings
     WHERE workshop_id = p_workshop_id
       AND member_id = p_member_id;

    COMMIT;
END$$

-- 7c) Ajout d'une œuvre à une exposition (transaction : statut + liaison)
CREATE PROCEDURE sp_add_artwork_to_exhibition(
    IN p_artwork_id    BIGINT,
    IN p_exhibition_id BIGINT
)
BEGIN
    DECLARE v_artwork_status VARCHAR(20);
    DECLARE v_exp_end DATE;

    START TRANSACTION;

    SELECT status INTO v_artwork_status
      FROM artworks
     WHERE id = p_artwork_id
       FOR UPDATE;

    SELECT end_date INTO v_exp_end
      FROM exhibitions
     WHERE id = p_exhibition_id;

    IF v_artwork_status = 'SOLD' THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A sold artwork cannot be added to an exhibition';
    END IF;

    IF v_exp_end < CURDATE() THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot add artwork to a past exhibition';
    END IF;

    INSERT INTO exhibition_artworks (exhibition_id, artwork_id)
    VALUES (p_exhibition_id, p_artwork_id)
    ON DUPLICATE KEY UPDATE exhibition_id = exhibition_id;

    UPDATE artworks
       SET status = 'EXHIBITED'
     WHERE id = p_artwork_id;

    COMMIT;
END$$

DELIMITER ;

