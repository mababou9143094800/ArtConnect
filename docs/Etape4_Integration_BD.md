# Étape 4 — Intégration de la base de données dans ArtConnect

## 1. Objectif

Remplacer les services « en mémoire » (InMemory*) de l'application Java ArtConnect par
une couche de persistance JDBC réelle connectée à la base MySQL `artconnect`.

---

## 2. Architecture retenue

### 2.1 Schéma en couches

```
┌─────────────────────────────────────────────────────────┐
│                        UI Layer                         │
│   ArtistController  ArtworkController  GalleryController│
│   WorkshopController  ExhibitionController  …           │
│            (JavaFX – inchangé)                          │
└────────────────────────┬────────────────────────────────┘
                         │ appelle
┌────────────────────────▼────────────────────────────────┐
│                    Service Layer                        │
│  «interface»          «impl JDBC»         «impl InMem»  │
│  ArtistService   →  JdbcArtistService   (conservé)      │
│  ArtworkService  →  JdbcArtworkService  (conservé)      │
│  GalleryService  →  JdbcGalleryService  (conservé)      │
│  WorkshopService →  JdbcWorkshopService (conservé)      │
│  CommunityService→  JdbcCommunityService(conservé)      │
└────────────────────────┬────────────────────────────────┘
                         │ utilise
┌────────────────────────▼────────────────────────────────┐
│                      DAO Layer                          │
│  «interface»          «impl JDBC»                       │
│  ArtistDao       →  JdbcArtistDao                       │
│  ArtworkDao      →  JdbcArtworkDao                      │
│  GalleryDao      →  JdbcGalleryDao                      │
│  ExhibitionDao   →  JdbcExhibitionDao                   │
│  WorkshopDao     →  JdbcWorkshopDao                     │
│  CommunityMemberDao → JdbcCommunityMemberDao            │
└────────────────────────┬────────────────────────────────┘
                         │ JDBC
┌────────────────────────▼────────────────────────────────┐
│              Connexion (util/config)                    │
│         ConnectionManager  ←  DatabaseConfig            │
└────────────────────────┬────────────────────────────────┘
                         │ TCP/3306
          ┌──────────────▼──────────────┐
          │      MySQL – artconnect     │
          └─────────────────────────────┘
```

### 2.2 Pattern DAO

Chaque entité métier possède :
- Une **interface DAO** dans `com.project.artconnect.dao` (ex. `ArtistDao`) — contrat
  d'accès aux données, indépendant de la technologie.
- Une **implémentation JDBC** dans `com.project.artconnect.persistence`
  (ex. `JdbcArtistDao`) — requêtes SQL via `PreparedStatement`.

Les contrôleurs JavaFX ne connaissent jamais les DAOs directement : ils passent
toujours par la couche Service, ce qui préserve l'architecture MVC d'origine.

### 2.3 Câblage — ServiceProvider

`ServiceProvider` (singleton statique) instancie les DAOs JDBC et les injecte dans
les services JDBC via constructeur :

```java
// util/ServiceProvider.java
JdbcArtistDao artistDao = new JdbcArtistDao();
...
artistService = new JdbcArtistService(artistDao);
```

L'interface de retour reste `ArtistService` : aucun contrôleur n'est modifié.

---

## 3. Connexion à la base de données

### 3.1 Configuration (`DatabaseConfig.java`)

```java
// config/DatabaseConfig.java
public class DatabaseConfig {
    public static final String URL  = "jdbc:mysql://localhost:3306/artconnect";
    public static final String USER = "root";
    public static final String PASSWORD = "root"; // à adapter
}
```

> **Pour adapter** : modifier `URL`, `USER` et `PASSWORD` selon votre installation MySQL.

### 3.2 `ConnectionManager`

```java
// util/ConnectionManager.java
public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
        DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
}
```

Chaque méthode DAO ouvre sa propre connexion dans un bloc `try-with-resources`,
ce qui garantit la fermeture même en cas d'exception.

---

## 4. Implémentation des DAOs JDBC

### 4.1 Principes communs

| Règle | Application |
|---|---|
| `PreparedStatement` obligatoire | Toutes les requêtes paramétrées — pas de concaténation de chaînes |
| `try-with-resources` | Connection, Statement et ResultSet fermés automatiquement |
| Transactions explicites | `setAutoCommit(false)` + `commit()` / `rollback()` sur les opérations multi-tables |
| `INSERT IGNORE` sur les tables de liaison | Évite les doublons sans lever d'exception |

### 4.2 JdbcArtistDao — extrait `save()`

```java
public void save(Artist artist) {
    String insertArtist = "INSERT INTO artists "
        + "(name, bio, birth_year, contact_email, phone, city, website, social_media, is_active)"
        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = ConnectionManager.getConnection()) {
        conn.setAutoCommit(false);
        try {
            long artistId;
            try (PreparedStatement ps = conn.prepareStatement(insertArtist,
                                            Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, artist.getName());
                ps.setString(2, artist.getBio());
                // ... autres champs ...
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    artistId = keys.getLong(1);
                }
            }
            saveDisciplines(conn, artistId, artist.getDisciplines()); // table liaison
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error saving artist: " + artist.getName(), e);
    }
}
```

Points notables :
- **Transaction** : l'insertion de l'artiste et de ses disciplines est atomique.
- **`getGeneratedKeys()`** : récupération de l'`id` auto-incrémenté pour lier les disciplines.
- **`getOrCreateDiscipline()`** : `SELECT` d'abord, puis `INSERT` si absent — évite
  les doublons dans la table `disciplines`.

### 4.3 JdbcArtistDao — extrait `findAll()`

```java
public List<Artist> findAll() {
    List<Artist> artists = new ArrayList<>();
    String sql = "SELECT id, name, bio, birth_year, contact_email, phone, "
               + "city, website, social_media, is_active FROM artists ORDER BY name";
    try (Connection conn = ConnectionManager.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            long id = rs.getLong("id");
            Artist artist = mapRow(rs);
            artist.setDisciplines(fetchDisciplinesForArtist(id)); // connexion séparée
            artists.add(artist);
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error fetching all artists", e);
    }
    return artists;
}
```

### 4.4 JdbcArtworkDao — gestion du statut (enum)

```java
// lecture depuis ResultSet
String statusStr = rs.getString("status");
if (statusStr != null) artwork.setStatus(Artwork.Status.valueOf(statusStr));

// écriture vers PreparedStatement
ps.setString(9, artwork.getStatus() != null ? artwork.getStatus().name() : "FOR_SALE");
```

Les valeurs de l'enum Java (`FOR_SALE`, `SOLD`, `EXHIBITED`) correspondent exactement
aux valeurs de l'ENUM MySQL — conversion directe par `valueOf` / `name()`.

### 4.5 JdbcExhibitionDao — JOIN multi-tables

```java
String sql = "SELECT e.title, e.start_date, e.end_date, e.description, "
           + "e.curator_name, e.theme, g.name AS gallery_name "
           + "FROM exhibitions e "
           + "JOIN galleries g ON e.gallery_id = g.id "
           + "ORDER BY e.start_date";
```

Le résultat contient directement le nom de la galerie : pas besoin de requête
supplémentaire pour reconstruire l'objet `Gallery` minimal.

### 4.6 JdbcWorkshopService — `bookWorkshop()` transactionnel

```java
public void bookWorkshop(Workshop workshop, CommunityMember member) {
    try (Connection conn = ConnectionManager.getConnection()) {
        conn.setAutoCommit(false);
        try {
            long workshopId = requireId(conn, "SELECT id FROM workshops WHERE title=?",
                                         workshop.getTitle(), "Workshop");
            long memberId   = requireId(conn, "SELECT id FROM community_members WHERE name=?",
                                         member.getName(), "Member");
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO bookings (workshop_id, member_id, payment_status)"
                  + " VALUES (?, ?, 'PENDING')")) {
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
        throw new RuntimeException(..., e);
    }
}
```

Le trigger `trg_bookings_before_insert_capacity` (défini en base) refuse
l'insertion si l'atelier est complet — la transaction est annulée automatiquement.

---

## 5. Implémentation des Services JDBC

### 5.1 Tableau récapitulatif

| Service | DAO(s) utilisé(s) | Méthodes avec logique supplémentaire |
|---|---|---|
| `JdbcArtistService` | `JdbcArtistDao` | `getAllDisciplines()` → requête directe table `disciplines` ; `searchArtists()` → filtre en mémoire sur `findAll()` |
| `JdbcArtworkService` | `JdbcArtworkDao` | `getArtworkByTitle()` → filtre en mémoire |
| `JdbcGalleryService` | `JdbcGalleryDao` + `JdbcExhibitionDao` | `getAllGalleries()` peuple `gallery.exhibitions` ; `getExhibitionsByGallery()` filtre par nom de galerie |
| `JdbcWorkshopService` | `JdbcWorkshopDao` | `bookWorkshop()` → transaction directe ; `getBookingsByMember()` → JOIN bookings/workshops/artists |
| `JdbcCommunityService` | `JdbcCommunityMemberDao` | `getReviewsByMember()` → JOIN reviews/artworks |

### 5.2 `JdbcCommunityService.getReviewsByMember()` — extrait

```java
String sql = "SELECT r.rating, r.comment, r.review_date, aw.title AS artwork_title "
           + "FROM reviews r "
           + "JOIN community_members cm ON r.reviewer_member_id = cm.id "
           + "JOIN artworks aw ON r.artwork_id = aw.id "
           + "WHERE cm.name = ?";
```

---

## 6. Fichiers créés / modifiés

### DAOs JDBC (`src/main/java/com/project/artconnect/persistence/`)

| Fichier | Statut | Tables touchées |
|---|---|---|
| `JdbcArtistDao.java` | Implémenté (remplace stub) | `artists`, `artist_disciplines`, `disciplines` |
| `JdbcArtworkDao.java` | Implémenté (remplace stub) | `artworks`, `artwork_tag_links`, `artwork_tags` |
| `JdbcGalleryDao.java` | Nouveau | `galleries` |
| `JdbcExhibitionDao.java` | Nouveau | `exhibitions`, `galleries` |
| `JdbcWorkshopDao.java` | Nouveau | `workshops`, `artists` |
| `JdbcCommunityMemberDao.java` | Nouveau | `community_members`, `member_favorite_disciplines` |

### Services JDBC (`src/main/java/com/project/artconnect/service/impl/`)

| Fichier | Statut |
|---|---|
| `JdbcArtistService.java` | Nouveau |
| `JdbcArtworkService.java` | Nouveau |
| `JdbcGalleryService.java` | Nouveau |
| `JdbcWorkshopService.java` | Nouveau |
| `JdbcCommunityService.java` | Nouveau |

### Câblage

| Fichier | Modification |
|---|---|
| `util/ServiceProvider.java` | Instancie les DAOs JDBC et injecte dans les services JDBC |
| `config/DatabaseConfig.java` | Paramètres de connexion MySQL (URL, user, password) |

---

## 7. Vérification du fonctionnement

### Prérequis

1. MySQL Server démarré sur `localhost:3306`
2. Base `artconnect` créée et schéma appliqué (`sql/artconnect_schema.sql`)
3. Données d'exemple insérées (`sql/artconnect_data.sql`)
4. Identifiants corrects dans `DatabaseConfig.java`

### Lancer l'application

```bash
mvn javafx:run
```

### Scénarios de test CRUD validés

| Écran | Action | Persistance vérifiée |
|---|---|---|
| Liste Artistes | Affichage | Données issues de `SELECT * FROM artists` |
| Artistes | Ajouter un artiste | `INSERT INTO artists` + disciplines |
| Artistes | Modifier un artiste | `UPDATE artists` + mise à jour disciplines |
| Artistes | Supprimer un artiste | `DELETE FROM artists` (cascade FK) |
| Œuvres | Affichage avec artiste | JOIN `artworks` ↔ `artists` |
| Œuvres | Ajouter une œuvre | `INSERT INTO artworks` + tags |
| Galeries | Affichage avec expositions | JOIN `galleries` ↔ `exhibitions` |
| Ateliers | Réserver | Transaction `INSERT INTO bookings` |
| Communauté | Avis d'un membre | JOIN `reviews` ↔ `artworks` |

---

## 8. Diagramme de classes simplifié (couche DAO + Service)

```
«interface»          «class»
ArtistService  ◄───  JdbcArtistService
                           │ utilise
                     «interface»    «class»
                     ArtistDao ◄─── JdbcArtistDao
                                         │ appelle
                                    ConnectionManager
                                         │
                                    DatabaseConfig

«interface»          «class»
GalleryService ◄───  JdbcGalleryService
                           │ utilise
                     GalleryDao ◄─── JdbcGalleryDao
                     ExhibitionDao ◄─ JdbcExhibitionDao

(même schéma pour Artwork / Workshop / Community)
```

---

*Livrable Étape 4 — ArtConnect Pro — EFREI INGE1 — TI603*
