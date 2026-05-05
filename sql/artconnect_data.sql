USE artconnect;

-- ─── DISCIPLINES ────────────────────────────────────────────
INSERT INTO disciplines (name) VALUES
    ('Peinture'),
    ('Sculpture'),
    ('Photographie'),
    ('Dessin'),
    ('Céramique');

-- ─── TAGS ────────────────────────────────────────────────────
INSERT INTO artwork_tags (name) VALUES
    ('Contemporain'),
    ('Abstrait'),
    ('Figuratif'),
    ('Nature morte'),
    ('Paysage');

-- ─── ARTISTES ────────────────────────────────────────────────
INSERT INTO artists (name, bio, birth_year, contact_email, phone, city, website, is_active) VALUES
    ('Claire Morin',
     'Peintre abstraite parisienne, inspirée par la lumière du Nord et les textures urbaines.',
     1985, 'claire.morin@art.fr', '0612345678', 'Paris',
     'https://clairemorin.art', TRUE),

    ('Luca Ferretti',
     'Sculpteur franco-italien, il travaille le bronze et le bois récupéré.',
     1978, 'luca.ferretti@atelier.it', '0698765432', 'Lyon',
     NULL, TRUE),

    ('Sophie Dubois',
     'Photographe documentaire spécialisée dans les portraits urbains.',
     1992, 'sophie.dubois@photo.fr', '0623456789', 'Bordeaux',
     'https://sophiephoto.fr', TRUE);

-- ─── DISCIPLINES ARTISTES ────────────────────────────────────
INSERT INTO artist_disciplines (artist_id, discipline_id) VALUES
    (1, 1), -- Claire → Peinture
    (1, 4), -- Claire → Dessin
    (2, 2), -- Luca  → Sculpture
    (2, 5), -- Luca  → Céramique
    (3, 3); -- Sophie → Photographie

-- ─── ŒUVRES ──────────────────────────────────────────────────
INSERT INTO artworks (artist_id, title, creation_year, type, medium, dimensions, description, price, status) VALUES
    (1, 'Éclats de nuit', 2022, 'Peinture', 'Acrylique sur toile',
     '120x90 cm', 'Composition abstraite évoquant Paris la nuit.', 1800.00, 'FOR_SALE'),

    (1, 'Brume urbaine', 2023, 'Peinture', 'Huile sur lin',
     '80x60 cm', 'Série urbaine, tons gris et bleus.', 1200.00, 'FOR_SALE'),

    (2, 'Racines', 2021, 'Sculpture', 'Bronze et bois flotté',
     '45x30x20 cm', 'Hybridation de matériaux naturels et industriels.', 3500.00, 'FOR_SALE'),

    (2, 'Équilibre', 2023, 'Sculpture', 'Céramique émaillée',
     '30x15x15 cm', 'Formes organiques en tension.', 950.00, 'FOR_SALE'),

    (3, 'Regard #12', 2024, 'Photographie', 'Impression pigmentaire',
     '60x40 cm', 'Portrait série "Regards de ville".', 450.00, 'FOR_SALE'),

    (3, 'Carrefour', 2023, 'Photographie', 'Impression argentique',
     '50x50 cm', 'Longue pose nocturne, place de la Bastille.', 380.00, 'FOR_SALE');

-- ─── TAGS DES ŒUVRES ─────────────────────────────────────────
INSERT INTO artwork_tag_links (artwork_id, tag_id) VALUES
    (1, 1), (1, 2),  -- Éclats de nuit → Contemporain, Abstrait
    (2, 1), (2, 2),  -- Brume urbaine  → Contemporain, Abstrait
    (3, 1), (3, 3),  -- Racines        → Contemporain, Figuratif
    (4, 5),          -- Équilibre      → Paysage
    (5, 1), (5, 3),  -- Regard #12     → Contemporain, Figuratif
    (6, 1), (6, 5);  -- Carrefour      → Contemporain, Paysage

-- ─── GALERIES ─────────────────────────────────────────────────
INSERT INTO galleries (name, address, owner_name, opening_hours, contact_phone, rating, website) VALUES
    ('Galerie Lumière', '14 rue des Beaux-Arts, 75006 Paris',
     'Marie Legrand', 'Mar-Sam 11h-19h', '0145678901', 4.50,
     'https://galerielumiere.fr'),

    ('Espace Confluences', '3 place Bellecour, 69002 Lyon',
     'Henri Bertrand', 'Mer-Dim 10h-18h', '0478901234', 4.20,
     NULL);

-- ─── EXPOSITIONS ─────────────────────────────────────────────
-- Dates futures pour satisfaire le trigger trg_exhibitions_before_insert_future_date
INSERT INTO exhibitions (gallery_id, title, start_date, end_date, description, curator_name, theme) VALUES
    (1, 'Abstraction Urbaine',
     DATE_ADD(CURDATE(), INTERVAL 15 DAY),
     DATE_ADD(CURDATE(), INTERVAL 75 DAY),
     'Une plongée dans la peinture abstraite contemporaine.',
     'Marie Legrand', 'Ville & Lumière'),

    (2, 'Matières & Formes',
     DATE_ADD(CURDATE(), INTERVAL 30 DAY),
     DATE_ADD(CURDATE(), INTERVAL 90 DAY),
     'Sculptures et céramiques entre tradition et modernité.',
     'Henri Bertrand', 'Matières premières');

-- ─── ATELIERS ─────────────────────────────────────────────────
INSERT INTO workshops (title, workshop_date, duration_minutes, max_participants,
                       price, instructor_artist_id, location, description, level) VALUES
    ('Initiation à la peinture abstraite',
     DATE_ADD(NOW(), INTERVAL 20 DAY),
     180, 10, 75.00, 1,
     'Galerie Lumière, Paris',
     'Atelier pratique : techniques acryliques et composition libre.', 'beginner'),

    ('Modelage céramique avancé',
     DATE_ADD(NOW(), INTERVAL 35 DAY),
     240, 8, 120.00, 2,
     'Atelier Ferretti, Lyon',
     'Tournage et émaillage – niveau intermédiaire/avancé.', 'advanced');

-- ─── MEMBRES ─────────────────────────────────────────────────
INSERT INTO community_members (name, email, birth_year, phone, city, membership_type) VALUES
    ('Alice Martin',  'alice.martin@gmail.com',  1995, '0611112222', 'Paris',     'premium'),
    ('Thomas Petit',  'thomas.petit@yahoo.fr',   1988, '0633334444', 'Lyon',      'free'),
    ('Emma Rousseau', 'emma.rousseau@orange.fr', 2001, '0655556666', 'Bordeaux',  'free');

-- ─── DISCIPLINES FAVORITES MEMBRES ───────────────────────────
INSERT INTO member_favorite_disciplines (member_id, discipline_id) VALUES
    (1, 1), (1, 3), -- Alice  → Peinture, Photographie
    (2, 2), (2, 5), -- Thomas → Sculpture, Céramique
    (3, 3);         -- Emma   → Photographie

-- ─── RÉSERVATIONS ────────────────────────────────────────────
-- Alice et Thomas s'inscrivent à l'atelier peinture (id=1)
INSERT INTO bookings (workshop_id, member_id, payment_status) VALUES
    (1, 1, 'PAID'),
    (1, 2, 'PENDING');

-- Alice s'inscrit aussi à l'atelier céramique (id=2)
INSERT INTO bookings (workshop_id, member_id, payment_status) VALUES
    (2, 1, 'PAID');

-- ─── AVIS ────────────────────────────────────────────────────
INSERT INTO reviews (reviewer_member_id, artwork_id, rating, comment) VALUES
    (1, 1, 5, 'Une toile saisissante, les contrastes sont remarquables.'),
    (1, 5, 4, 'Portrait très expressif, cadrage original.'),
    (2, 3, 5, 'Sculpture magistrale, la fusion bronze/bois est parfaite.'),
    (3, 6, 4, 'Belle maîtrise de la longue pose. Ambiance nocturne réussie.'),
    (3, 2, 3, 'Jolie œuvre, mais la palette me laisse un peu froid.');
