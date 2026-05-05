USE artconnect;

-- SCÉNARIO 1 : Inscription d'un membre à un atelier
--   Opérations atomiques :
--   1. Vérifier que le membre n'est pas déjà inscrit
--   2. Vérifier la capacité disponible (trigger)
--   3. Créer la réservation
--   4. Mettre à jour current_participants (trigger)

-- Cas nominal : Emma (id=3) s'inscrit à l'atelier peinture (id=1)
CALL sp_register_booking(1, 3, 'PENDING');

-- Vérification
SELECT w.title,
       w.current_participants,
       w.max_participants,
       b.payment_status
FROM workshops w
JOIN bookings b ON b.workshop_id = w.id
WHERE w.id = 1 AND b.member_id = 3;

-- Cas d'erreur : double inscription (doit échouer)
-- CALL sp_register_booking(1, 3, 'PENDING');

-- SCÉNARIO 2 : Annulation d'une réservation
--   Opérations atomiques :
--   1. Marquer CANCELLED
--   2. Supprimer la ligne (decrement via trigger + audit via trigger)

-- Thomas (id=2) annule son inscription à l'atelier peinture (id=1)
CALL sp_cancel_booking(1, 2);

-- Vérification : compteur décrémenté, ligne absente, audit présent
SELECT current_participants FROM workshops WHERE id = 1;

SELECT * FROM audit_log WHERE table_name = 'bookings' ORDER BY changed_at DESC LIMIT 5;

-- SCÉNARIO 3 : Ajout d'une œuvre à une exposition
--   Opérations atomiques :
--   1. Vérifier que l'œuvre n'est pas vendue
--   2. Vérifier que l'exposition n'est pas passée
--   3. Créer le lien exhibition_artworks
--   4. Mettre à jour le statut de l'œuvre → EXHIBITED
--   5. Trigger audit_log détecte le changement de statut

-- Ajout de "Éclats de nuit" (id=1) à "Abstraction Urbaine" (id=1)
CALL sp_add_artwork_to_exhibition(1, 1);

-- Vérification : statut EXHIBITED + lien créé + audit
SELECT title, status FROM artworks WHERE id = 1;

SELECT exhibition_id, artwork_id FROM exhibition_artworks WHERE artwork_id = 1;

SELECT * FROM audit_log WHERE table_name = 'artworks' ORDER BY changed_at DESC LIMIT 3;

-- SCÉNARIO 4 : Transaction explicite multi-opérations
--   Inscription en lot de deux membres (atomique : tout ou rien)
--   Si l'un échoue, on annule tout.

START TRANSACTION;

-- Réinscription de Thomas (id=2) à l'atelier peinture (id=1)
INSERT INTO bookings (workshop_id, member_id, payment_status)
VALUES (1, 2, 'PAID');

-- Ajout de "Brume urbaine" (id=2) à l'exposition "Abstraction Urbaine" (id=1)
INSERT INTO exhibition_artworks (exhibition_id, artwork_id) VALUES (1, 2);

UPDATE artworks SET status = 'EXHIBITED' WHERE id = 2;

-- Si tout est OK : valider
COMMIT;

-- Vérification finale
SELECT 'Atelier peinture – participants' AS info,
       current_participants, max_participants
FROM workshops WHERE id = 1;

SELECT 'Statut œuvres exposées' AS info,
       title, status
FROM artworks WHERE status = 'EXHIBITED';

SELECT 'Audit log (5 derniers)' AS info,
       table_name, action, record_id, old_value, new_value, changed_at
FROM audit_log ORDER BY changed_at DESC LIMIT 5;

-- TEST DES VUES

-- Vue publique œuvres (pas d'email ni téléphone)
SELECT * FROM v_artworks_public ORDER BY avg_rating DESC;

-- Vue membres avec email masqué
SELECT * FROM v_members_masked;

-- Vue activité membres
SELECT * FROM v_member_activity;

-- Vue portfolio artistes
SELECT * FROM v_artist_portfolio;

-- Vue événements à venir
SELECT * FROM v_upcoming_events ORDER BY event_start;

-- ═══════════════════════════════════════════════════════════════
-- TEST DES TRIGGERS (erreurs attendues)
-- ═══════════════════════════════════════════════════════════════

-- Test trigger date passée (doit échouer)
-- INSERT INTO exhibitions (gallery_id, title, start_date, end_date)
-- VALUES (1, 'Test passé', '2020-01-01', '2020-02-01');

-- Test note invalide (doit échouer)
-- INSERT INTO reviews (reviewer_member_id, artwork_id, rating, comment)
-- VALUES (1, 4, 6, 'Note invalide');

-- Test atelier complet : remplir jusqu'à max, puis tenter une inscription de trop
-- (max_participants de l'atelier 2 = 8 ; insérer 8 bookings pour tester)
