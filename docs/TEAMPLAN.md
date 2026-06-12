# Teamplan – Glauser Illnau AG Serviceauftrag-Verwaltung

## Team
| Name | GitHub | Rolle |
|---|---|---|
| Kenan Cantekin | `Cantekin-bzra` | Architektur & Backend |
| Sami Bouddat | `Bouddat-bzra` | Datenbank & Datenmodell |
| Samuel Pereira Paredes | `paredes-bzra` | Analyse, UI & Frontend |

---

## Git-Branches
| Branch | Zuständig |
|---|---|
| `main` | Stabiler Stand – nur via Pull Request |
| `feature/backend` | Kenan |
| `feature/database` | Sami |
| `feature/frontend` | Samuel |

---

## Phase 0 – Vorbereitung (Alle gemeinsam)

| Schritt | Aufgabe | Status |
|---|---|---|
| 1 | Aufgabenstellung lesen und verstehen | Erledigt |
| 2 | Team bilden, Rollen und Verantwortlichkeiten festlegen | Erledigt |
| 3 | GitHub-Repository erstellen, Ordnerstruktur anlegen, Collaborators einladen | Erledigt |
| 4 | Entwicklungsumgebung einrichten (IntelliJ IDEA / VS Code, Java 21, Node.js, Git) | Offen |

---

## Phase 1 – Analyse & Entwurf

### Schritt 5 – UML-Anwendungsfalldiagramm
**Zuständig:** Samuel  
**Branch:** `feature/frontend`  
**Ziel:** Alle Akteure (GL/Admin, Bereichsleiter, Mitarbeiter) und deren Aktionen als Use-Case-Diagramm darstellen.  
**Lieferobjekt:** `docs/uml/use-case-diagramm.png` + Quelldatei (z.B. PlantUML oder draw.io)

### Schritt 6 – Zustandsdiagramm des Auftrags
**Zuständig:** Samuel  
**Branch:** `feature/frontend`  
**Ziel:** Die 5 Auftragsstatus (ERFASST → DISPONIERT → AUSGEFÜHRT → FREIGEGEBEN → VERRECHNET) als UML-Zustandsdiagramm abbilden inkl. Transitionen und auslösenden Ereignissen.  
**Lieferobjekt:** `docs/uml/zustandsdiagramm.png`

### Schritt 7 – ERD / ERM mit Attributen, Datentypen, Wertebereichen
**Zuständig:** Sami  
**Branch:** `feature/database`  
**Ziel:** Vollständiges Entity-Relationship-Diagramm mit allen Tabellen, Attributen, Primär- und Fremdschlüsseln, Datentypen (VARCHAR, DATE, ENUM, etc.) und Wertebereichen.  
**Lieferobjekt:** `docs/erd/erd.png` + `docs/erd/erm-attribute.md`

### Schritt 8 – Klassendiagramm (fakultativ)
**Zuständig:** Samuel  
**Branch:** `feature/frontend`  
**Ziel:** UML-Klassendiagramm der wichtigsten Java-Klassen (Entities, Services, Controller) mit Attributen, Methoden und Beziehungen.  
**Lieferobjekt:** `docs/uml/klassendiagramm.png`

### Schritt 9 – Architekturentscheid
**Zuständig:** Kenan  
**Branch:** `feature/backend`  
**Ziel:** Technologieentscheid dokumentieren: Thymeleaf + JavaScript (SSR) als Frontend-Ansatz, Spring Boot 4 REST-API, PostgreSQL. Schnittstellen zwischen Frontend und Backend definieren (REST-Endpunkte).  
**Lieferobjekt:** `docs/architektur.md`

---

## Phase 2 – UI-Entwurf

### Schritt 10 – Wireframes
**Zuständig:** Samuel  
**Branch:** `feature/frontend`  
**Ziel:** Handskizzen oder digitale Wireframes für alle 5 Ansichten:
- Auftragserfassung (Formular)
- Auftragsliste nach Status
- Dispositionsansicht (Mitarbeiter zuweisen, Termin)
- Rapport-Ansicht (Ausführung bestätigen)
- Druckansicht / Auftragsdokument  

**Lieferobjekt:** `docs/wireframes/` (PNG oder PDF)

### Schritt 11 – Klickbare UI-Mockups
**Zuständig:** Samuel  
**Branch:** `feature/frontend`  
**Ziel:** HTML/CSS-Mockups ohne Backend-Anbindung – alle Formulare, Tabellen, Buttons sind sichtbar und navigierbar, aber ohne echte Daten.  
**Lieferobjekt:** `frontend/mockups/`

---

## Phase 3 – Datenbank

### Schritt 12 – SQL-Skript
**Zuständig:** Sami  
**Branch:** `feature/database`  
**Ziel:** Vollständiges SQL-Skript mit:
- CREATE TABLE für alle Tabellen
- PRIMARY KEY, FOREIGN KEY, NOT NULL, UNIQUE Constraints
- ENUM/CHECK-Constraints für Statusfelder und Arbeitstypen
- Indizes für häufig abgefragte Felder  

**Lieferobjekt:** `sql/01_schema.sql`

### Schritt 13 – Testdaten
**Zuständig:** Sami  
**Branch:** `feature/database`  
**Ziel:** SQL INSERT-Statements mit mindestens 10 realistischen Aufträgen in verschiedenen Statuswerten, inkl. Kunden, Mitarbeitern und Bereichsleitern.  
**Lieferobjekt:** `sql/02_testdaten.sql`

### Schritt 14 – Supabase-Setup
**Zuständig:** Sami  
**Branch:** `feature/database`  
**Ziel:** PostgreSQL-Datenbank auf Supabase erstellen, Schema und Testdaten einspielen, Verbindungs-URL und Zugangsdaten sicher im Projekt hinterlegen (`.env` / `application.properties`).  
**Lieferobjekt:** `docs/supabase-setup.md` (Anleitung ohne Passwörter)

---

## Phase 4 – Backend (Spring Boot 4 / Spring 7)

### Schritt 15 – Projekt-Setup
**Zuständig:** Kenan  
**Branch:** `feature/backend`  
**Ziel:** Spring Boot 4 Projekt via Spring Initializr erstellen mit Dependencies: Spring Web, Spring Data JPA, PostgreSQL Driver, Thymeleaf, Validation, Lombok.  
**Lieferobjekt:** `backend/` (komplettes Maven/Gradle-Projekt)

### Schritt 16 – JPA-Entities + Repositories
**Zuständig:** Kenan  
**Branch:** `feature/backend`  
**Ziel:** Alle Datenbank-Tabellen als Java-Klassen mit `@Entity`, `@Table`, `@Column` etc. abbilden. Spring Data JPA Repositories für jede Entity erstellen.  
**Lieferobjekt:** `backend/src/main/java/.../entity/` und `.../repository/`

### Schritt 17 – Services mit Statuswechsel-Logik
**Zuständig:** Kenan  
**Branch:** `feature/backend`  
**Ziel:** Service-Klassen für alle Geschäftsregeln implementieren: Auftrag erfassen, disponieren, als ausgeführt markieren, freigeben, verrechnen. Statusübergänge validieren (kein Überspringen von Zuständen).  
**Lieferobjekt:** `backend/src/main/java/.../service/`

### Schritt 18 – Controller / REST-Endpunkte
**Zuständig:** Kenan  
**Branch:** `feature/backend`  
**Ziel:** REST-Controller für alle Operationen:
- `GET /auftraege` – Liste aller Aufträge
- `POST /auftraege` – Neuen Auftrag erfassen
- `PUT /auftraege/{id}/disponieren` – Auftrag disponieren
- `PUT /auftraege/{id}/ausgefuehrt` – Als ausgeführt markieren
- `PUT /auftraege/{id}/freigeben` – Freigabe durch BL
- `PUT /auftraege/{id}/verrechnet` – Als verrechnet markieren  

**Lieferobjekt:** `backend/src/main/java/.../controller/`

### Schritt 19 – DB-Verbindung zu Supabase
**Zuständig:** Kenan  
**Branch:** `feature/backend`  
**Ziel:** `application.properties` mit Supabase-Verbindungsdaten konfigurieren, Verbindung testen, JPA-Dialect setzen.  
**Lieferobjekt:** `backend/src/main/resources/application.properties`

---

## Phase 5 – Frontend-Integration

### Schritt 20 – Anbindung, Validierung, Druckfunktion
**Zuständig:** Samuel  
**Branch:** `feature/frontend`  
**Ziel:**
- Thymeleaf-Templates an REST-Endpunkte anbinden
- Clientseitige Formularvalidierung mit JavaScript
- Kontextuales Feedback (Bestätigungsmeldungen, Fehlermeldungen)
- Druckfunktion für das Auftragsdokument (Browser-Print / PDF)  

**Lieferobjekt:** `frontend/templates/` + `frontend/static/js/`

---

## Phase 6 – Tests

### Schritt 21a – Unit-Tests
**Zuständig:** Kenan  
**Branch:** `feature/backend`  
**Ziel:** JUnit 5 Tests für Service-Klassen (Statuswechsel-Logik), Validierungen und Edge Cases.  
**Lieferobjekt:** `backend/src/test/java/.../service/`

### Schritt 21b – Integrationstests
**Zuständig:** Sami  
**Branch:** `feature/database`  
**Ziel:** Spring Boot Integrationstests für Repository-Operationen und DB-Verbindung.  
**Lieferobjekt:** `backend/src/test/java/.../repository/`

---

## Abhängigkeiten & Reihenfolge

```
Phase 0 (Alle)
    │
    ├── Sami: ERD (Schritt 7) ──────────────────────────────────────────────┐
    │         │                                                               │
    │         └── Sami: SQL-Schema (Schritt 12) ──► Kenan: Entities (16)    │
    │                   │                                    │                │
    │                   └── Sami: Supabase (14) ──► Kenan: DB-Conn (19)     │
    │                                                        │                │
    ├── Samuel: Wireframes (10-11) ────────────────► Samuel: Integration (20)┤
    │                                                        │                │
    └── Kenan: Setup (15) ──► Services (17) ──► Controller (18) ────────────┘
                                                             │
                                                    Tests (21a + 21b)
```

---

## Workload-Übersicht

| Person | Schritte | Anzahl Tasks |
|---|---|---|
| Kenan | 9, 15, 16, 17, 18, 19, 21a | 7 |
| Sami | 7, 12, 13, 14, 21b | 5 |
| Samuel | 5, 6, 8, 10, 11, 20 | 6 |

*Phase 0 (Schritte 1–4) wurde gemeinsam erledigt.*
