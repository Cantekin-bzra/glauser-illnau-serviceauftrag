# Glauser Illnau AG – Serviceauftrag-Verwaltung

Schulprojekt: Webanwendung zur Verwaltung von Serviceaufträgen für Glauser Illnau AG (Sanitär & Heizung, Illnau-Effretikon).

---

## Team

| Name | GitHub | Aufgaben |
|---|---|---|
| Kenan Cantekin | `Cantekin-bzra` | Architektur, Backend, Tests |
| Sami Bouddat | `Bouddat-bzra` | Datenbank, ERD, SQL, Integrationstests |
| Samuel Pereira Paredes | `paredes-bzra` | Analyse, UML, Wireframes, Mockups, Frontend |

---

## Technologie-Stack

| Bereich | Technologie |
|---|---|
| Backend | Spring Boot 3.4.1, Spring Data JPA, Hibernate |
| Frontend | Thymeleaf (SSR), JavaScript (Validierung) |
| Datenbank | PostgreSQL 15 (lokal) |
| Tests | JUnit 5, Mockito, H2 In-Memory |
| Build | Maven 3.9 |
| Java | Java 23 |

---

## Auftragsstatus-Workflow

```
ERFASST → DISPONIERT → AUSGEFUEHRT → FREIGEGEBEN → VERRECHNET
```

| Status | Aktion | Zuständig |
|---|---|---|
| ERFASST | Auftrag wird erfasst | GL / Admin |
| DISPONIERT | Mitarbeiter + Termin zugewiesen | Bereichsleiter |
| AUSGEFUEHRT | Rapport erfasst, Arbeit abgeschlossen | Mitarbeiter |
| FREIGEGEBEN | Rapport geprüft und freigegeben | Bereichsleiter |
| VERRECHNET | Rechnung gestellt | Administration |

---

## Projektstruktur

```
glauser-illnau-serviceauftrag/
├── docs/
│   ├── TEAMPLAN.md               Phasenpläne und Zuständigkeiten
│   ├── architektur.md            Technologie- und Architekturentscheid
│   ├── datenbank-setup.md        Anleitung lokale PostgreSQL-Einrichtung
│   ├── erd/
│   │   └── serviceauftrag_erd.html   Entity-Relationship-Diagramm
│   ├── uml/
│   │   ├── use-case-diagramm.png
│   │   ├── zustandsdiagramm.png
│   │   └── klassendiagramm.html
│   └── wireframes/               5 HTML-Wireframes (Samuel)
├── frontend/
│   └── mockups/                  5 klickbare HTML-Mockups (Samuel)
├── sql/
│   ├── 01_schema.sql             PostgreSQL-Schema (ENUMs, Tabellen, Indizes)
│   └── 02_testdaten.sql          Testdaten (Benutzer, Kunden, Aufträge)
└── backend/                      Spring Boot Anwendung
    └── src/
        ├── main/
        │   ├── java/.../
        │   │   ├── entity/       Auftrag, Benutzer, Kunde, Rapport, Rechnung, Arbeitstyp
        │   │   ├── repository/   Spring Data JPA Repositories
        │   │   ├── service/      AuftragService, RapportService, RechnungService
        │   │   ├── controller/   AuftragController, RapportController
        │   │   ├── dto/          AuftragDTO, DispositionDTO, RapportDTO
        │   │   └── enums/        AuftragStatus, Rolle
        │   └── resources/
        │       ├── templates/    Thymeleaf-Templates (liste, erfassen, detail, …)
        │       └── static/       CSS (main.css, print.css), JS (validierung.js, adresse.js)
        └── test/
            ├── java/.../
            │   ├── service/      AuftragServiceTest, RapportServiceTest (Unit)
            │   └── repository/   AuftragRepositoryTest, BenutzerRepositoryTest (Integration)
            └── resources/
                └── application-test.properties   H2-Konfiguration für Tests
```

---

## Lokale Einrichtung

### Voraussetzungen
- Java 21+
- Maven 3.9+
- PostgreSQL 15 (lokal laufend)

### Datenbank einrichten

```sql
-- Als postgres-Superuser:
CREATE USER sa_user WITH PASSWORD 'changeme';
CREATE DATABASE serviceauftrag OWNER sa_user;
```

```bash
psql -U sa_user -d serviceauftrag -f sql/01_schema.sql
psql -U sa_user -d serviceauftrag -f sql/02_testdaten.sql
```

Detaillierte Anleitung: [`docs/datenbank-setup.md`](docs/datenbank-setup.md)

### Anwendung starten

```bash
cd backend
mvn spring-boot:run
```

Die Anwendung ist erreichbar unter: `http://localhost:8080/auftraege`

Das DB-Passwort kann über die Umgebungsvariable `DB_PASSWORD` gesetzt werden (Standard: `changeme`).

### Tests ausführen

```bash
cd backend
mvn test
```

Tests laufen mit H2 In-Memory – keine laufende PostgreSQL-Datenbank nötig.

---

## URL-Übersicht

| URL | Beschreibung |
|---|---|
| `GET /auftraege` | Auftragsliste (alle Status) |
| `GET /auftraege/neu` | Neuen Auftrag erfassen |
| `GET /auftraege/{id}` | Auftragsdetail |
| `GET /auftraege/{id}/disponieren` | Dispositionsformular |
| `POST /auftraege/{id}/ausgefuehrt` | Auftrag als ausgeführt markieren |
| `POST /auftraege/{id}/freigeben` | Auftrag freigeben |
| `POST /auftraege/{id}/verrechnet` | Auftrag als verrechnet markieren |
| `GET /auftraege/{id}/drucken` | Druckansicht / PDF |
| `POST /auftraege/{id}/rapport` | Rapport erfassen |
