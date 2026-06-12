# Glauser Illnau AG – Serviceauftrag-Verwaltung

Webanwendung zur Verwaltung von Serviceaufträgen für Glauser Illnau AG (Sanitärunternehmen).

## Technologie-Stack
- **Backend:** Spring Boot 4 / Spring Framework 7 / Spring Data JPA
- **Frontend:** Thymeleaf + JavaScript
- **Datenbank:** PostgreSQL (Supabase)

## Ordnerstruktur
```
docs/       – Dokumentation, ERD, UML, UI-Sketches
sql/        – Datenbankschema und Seed-Daten
backend/    – Spring Boot Anwendung
frontend/   – Templates / statische Ressourcen
```

## Workflow
1. **ERFASST** – GL/Admin nimmt Auftrag an
2. **DISPONIERT** – Bereichsleiter weist Mitarbeiter zu
3. **AUSGEFÜHRT** – Mitarbeiter erledigt und rapportiert
4. **FREIGEGEBEN** – Bereichsleiter prüft und gibt frei
5. **VERRECHNET** – Administration stellt Rechnung

## Team
- Kenan Cantekin
- Samuel Pereira Paredes
- Sami Bouddat
