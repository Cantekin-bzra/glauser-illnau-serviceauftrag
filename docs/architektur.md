# Architekturentscheid – Glauser Illnau AG Serviceauftrag-Verwaltung

**Zuständig:** Kenan Cantekin  
**Schritt:** 9

---

## 1. Gewählte Architektur

**Schichtenarchitektur (Layered Architecture)** mit serverseitigem Rendering:

```
┌─────────────────────────────────────────────┐
│           Browser (Client)                  │
│   Thymeleaf HTML + JavaScript (Validierung) │
└──────────────────┬──────────────────────────┘
                   │ HTTP
┌──────────────────▼──────────────────────────┐
│         Controller Layer                    │
│         Spring MVC (@Controller)            │
├─────────────────────────────────────────────┤
│         Service Layer                       │
│         Geschäftslogik + Statuswechsel      │
├─────────────────────────────────────────────┤
│         Repository Layer                    │
│         Spring Data JPA (Interfaces)        │
├─────────────────────────────────────────────┤
│         Datenbank                           │
│         PostgreSQL auf Supabase             │
└─────────────────────────────────────────────┘
```

---

## 2. Technologieentscheid

### Frontend: Thymeleaf + JavaScript

| Kriterium | Thymeleaf + JS | React |
|---|---|---|
| Spring Boot Integration | Nativ, kein Zusatzaufwand | Separates Projekt nötig |
| Lernaufwand | Gering | Höher (JSX, State, Hooks) |
| Deployment | Ein einziges JAR | Frontend separat bauen |
| Validierung | JS clientseitig + Bean Validation serverseitig | JS clientseitig |
| Druckfunktion | Einfach via CSS `@media print` | Gleich |

**Entscheid: Thymeleaf + JavaScript** – direkte Integration in Spring Boot, ein Deployment-Artefakt, ausreichend für die Anforderungen.

### Backend: Spring Boot 4 / Spring Framework 7

- Spring Boot 4 mit Spring MVC für Controller
- Spring Data JPA (Hibernate) für Datenbankzugriff
- Bean Validation (`@Valid`) für serverseitige Eingabeprüfung
- Lombok für weniger Boilerplate-Code

### Datenbank: PostgreSQL auf Supabase

- Verwaltete PostgreSQL-Instanz, kein eigener Datenbankserver nötig
- Verbindung via JDBC-URL in `application.properties`
- Spring Data JPA verwaltet Schema-Synchronisation (`ddl-auto=validate`)

### Build: Maven

- Standardisiertes Dependency-Management
- Spring Initializr-kompatibel

---

## 3. Paketstruktur Backend

```
backend/src/main/java/ch/glauserillnau/serviceauftrag/
├── controller/
│   ├── AuftragController.java
│   └── RapportController.java
├── service/
│   ├── AuftragService.java
│   ├── RapportService.java
│   └── RechnungService.java
├── repository/
│   ├── AuftragRepository.java
│   ├── KundeRepository.java
│   ├── BenutzerRepository.java
│   ├── RapportRepository.java
│   └── RechnungRepository.java
├── entity/
│   ├── Auftrag.java
│   ├── Kunde.java
│   ├── Benutzer.java
│   ├── Rapport.java
│   ├── Rechnung.java
│   └── Arbeitstyp.java
├── dto/
│   ├── AuftragDTO.java
│   ├── DispositionDTO.java
│   └── RapportDTO.java
└── enums/
    ├── AuftragStatus.java
    └── Rolle.java
```

---

## 4. REST-Schnittstellen (Controller-Routen)

### Aufträge

| Methode | URL | Beschreibung | Zugriff |
|---|---|---|---|
| `GET` | `/auftraege` | Liste aller Aufträge (filterbar nach Status) | GL/Admin, BL |
| `GET` | `/auftraege/neu` | Formular: Neuen Auftrag erfassen | GL/Admin |
| `POST` | `/auftraege` | Auftrag speichern | GL/Admin |
| `GET` | `/auftraege/{id}` | Auftragsdetail anzeigen | Alle |
| `GET` | `/auftraege/{id}/disponieren` | Formular: Auftrag disponieren | BL |
| `POST` | `/auftraege/{id}/disponieren` | Disposition speichern (MA + Termin) | BL |
| `POST` | `/auftraege/{id}/ausgefuehrt` | Auftrag als ausgeführt markieren | MA |
| `POST` | `/auftraege/{id}/freigeben` | Rapport freigeben | BL |
| `POST` | `/auftraege/{id}/verrechnet` | Auftrag als verrechnet markieren | GL/Admin |
| `GET` | `/auftraege/{id}/drucken` | Druckansicht des Auftragsdokuments | Alle |

### Rapporte

| Methode | URL | Beschreibung | Zugriff |
|---|---|---|---|
| `GET` | `/auftraege/{id}/rapport` | Formular: Rapport erfassen | MA |
| `POST` | `/auftraege/{id}/rapport` | Rapport speichern | MA |

### Benutzer (intern)

| Methode | URL | Beschreibung | Zugriff |
|---|---|---|---|
| `GET` | `/benutzer` | Liste aller Benutzer | BL, Admin |

---

## 5. Statusübergänge (Geschäftsregeln)

Nur folgende Übergänge sind erlaubt — jeder andere wirft eine Exception:

```
ERFASST ──► DISPONIERT    (BL: Mitarbeiter + Termin setzen)
DISPONIERT ──► AUSGEFUEHRT (MA: Rapport erfassen)
AUSGEFUEHRT ──► FREIGEGEBEN (BL: Rapport prüfen und freigeben)
FREIGEGEBEN ──► VERRECHNET  (GL/Admin: Rechnung erstellen)
```

---

## 6. Frontend-Struktur (Thymeleaf Templates)

```
backend/src/main/resources/
├── templates/
│   ├── layout.html              (Basis-Layout mit Navigation)
│   ├── auftrag/
│   │   ├── liste.html           (Auftragsliste nach Status)
│   │   ├── erfassen.html        (Erfassungsformular)
│   │   ├── detail.html          (Auftragsdetail)
│   │   ├── disponieren.html     (Dispositionsformular)
│   │   ├── rapport.html         (Rapport-Formular)
│   │   └── drucken.html         (Druckansicht)
└── static/
    ├── css/
    │   ├── main.css
    │   └── print.css
    └── js/
        ├── validierung.js       (Clientseitige Formularvalidierung)
        └── adresse.js           (Objekt-/Verrechnungsadresse Toggle)
```

---

## 7. Konfiguration

`application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://<supabase-host>:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=<secret>
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.thymeleaf.cache=false
```

Sensible Werte (Passwort, URL) werden **nicht** ins Git eingecheckt — sie werden als Umgebungsvariablen oder in einer lokalen `.env`-Datei gesetzt, die in `.gitignore` eingetragen ist.
