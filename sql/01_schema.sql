-- =============================================================
-- Glauser Illnau AG – Serviceauftrag-Verwaltung
-- Schema: 01_schema.sql
-- Zuständig: Sami Bouddat
-- Datenbank: PostgreSQL 15+
-- =============================================================

-- Sicherstellen dass Tabellen frisch erstellt werden
DROP TABLE IF EXISTS auftrag_arbeitstyp CASCADE;
DROP TABLE IF EXISTS rechnung        CASCADE;
DROP TABLE IF EXISTS rapport         CASCADE;
DROP TABLE IF EXISTS auftrag         CASCADE;
DROP TABLE IF EXISTS arbeitstyp      CASCADE;
DROP TABLE IF EXISTS kunde           CASCADE;
DROP TABLE IF EXISTS benutzer        CASCADE;

DROP TYPE IF EXISTS auftrag_status CASCADE;
DROP TYPE IF EXISTS benutzer_rolle CASCADE;

-- =============================================================
-- ENUMS
-- =============================================================

CREATE TYPE auftrag_status AS ENUM (
    'ERFASST',
    'DISPONIERT',
    'AUSGEFUEHRT',
    'FREIGEGEBEN',
    'VERRECHNET'
);

CREATE TYPE benutzer_rolle AS ENUM (
    'ADMIN',
    'BEREICHSLEITER',
    'MITARBEITER'
);

-- =============================================================
-- TABELLE: benutzer
-- Mitarbeiter, Bereichsleiter und Admins der Firma
-- =============================================================

CREATE TABLE benutzer (
    benutzer_id     SERIAL          PRIMARY KEY,
    benutzername    VARCHAR(50)     NOT NULL UNIQUE,
    vorname         VARCHAR(100)    NOT NULL,
    nachname        VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    NOT NULL UNIQUE,
    passwort_hash   VARCHAR(255)    NOT NULL,
    rolle           benutzer_rolle  NOT NULL,
    aktiv           BOOLEAN         NOT NULL DEFAULT TRUE,
    erstellt_am     TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- =============================================================
-- TABELLE: kunde
-- Kunden / Kontaktpersonen die Aufträge erteilen
-- =============================================================

CREATE TABLE kunde (
    kunden_id   SERIAL          PRIMARY KEY,
    vorname     VARCHAR(100)    NOT NULL,
    nachname    VARCHAR(100)    NOT NULL,
    firma       VARCHAR(150),
    telefon     VARCHAR(30)     NOT NULL,
    natel       VARCHAR(30),
    email       VARCHAR(150),
    strasse     VARCHAR(150)    NOT NULL,
    plz         VARCHAR(10)     NOT NULL,
    ort         VARCHAR(100)    NOT NULL,
    erstellt_am TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_kunde_telefon CHECK (LENGTH(TRIM(telefon)) >= 7)
);

-- =============================================================
-- TABELLE: arbeitstyp
-- Reparatur, Sanitär, Heizung, Garantie
-- =============================================================

CREATE TABLE arbeitstyp (
    arbeitstyp_id   SERIAL          PRIMARY KEY,
    bezeichnung     VARCHAR(50)     NOT NULL UNIQUE
);

-- =============================================================
-- TABELLE: auftrag
-- Kernentität – ein Serviceauftrag
-- =============================================================

CREATE TABLE auftrag (
    auftrag_id              SERIAL          PRIMARY KEY,
    kunden_id               INT             NOT NULL REFERENCES kunde(kunden_id),
    bereichsleiter_id       INT             REFERENCES benutzer(benutzer_id),
    mitarbeiter_id          INT             REFERENCES benutzer(benutzer_id),

    -- Auftragsdaten
    titel                   VARCHAR(200)    NOT NULL,
    beschreibung            TEXT            NOT NULL,
    status                  auftrag_status  NOT NULL DEFAULT 'ERFASST',

    -- Zeitstempel
    erfassungsdatum         DATE            NOT NULL DEFAULT CURRENT_DATE,
    erfassungszeit          TIME            NOT NULL DEFAULT CURRENT_TIME,
    terminwunsch            VARCHAR(200),
    termin_geplant          DATE,
    ausfuehrungsdatum       DATE,
    freigabe_datum          DATE,
    verrechnet_am           DATE,

    -- Objektadresse (kann von Kundenadresse abweichen)
    objekt_gleich_kunde     BOOLEAN         NOT NULL DEFAULT TRUE,
    objekt_strasse          VARCHAR(150),
    objekt_plz              VARCHAR(10),
    objekt_ort              VARCHAR(100),

    -- Verrechnungsadresse (kann von Kundenadresse abweichen)
    verrechnung_abweichend  BOOLEAN         NOT NULL DEFAULT FALSE,
    verrechnung_vorname     VARCHAR(100),
    verrechnung_nachname    VARCHAR(100),
    verrechnung_strasse     VARCHAR(150),
    verrechnung_plz         VARCHAR(10),
    verrechnung_ort         VARCHAR(100),

    erstellt_am             TIMESTAMP       NOT NULL DEFAULT NOW(),
    aktualisiert_am         TIMESTAMP       NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT chk_status_mitarbeiter
        CHECK (status = 'ERFASST' OR mitarbeiter_id IS NOT NULL),
    CONSTRAINT chk_termin_geplant
        CHECK (status = 'ERFASST' OR termin_geplant IS NOT NULL),
    CONSTRAINT chk_ausfuehrungsdatum
        CHECK (status NOT IN ('AUSGEFUEHRT','FREIGEGEBEN','VERRECHNET') OR ausfuehrungsdatum IS NOT NULL),
    CONSTRAINT chk_freigabe_datum
        CHECK (status NOT IN ('FREIGEGEBEN','VERRECHNET') OR freigabe_datum IS NOT NULL),
    CONSTRAINT chk_objekt_felder
        CHECK (objekt_gleich_kunde = TRUE OR (objekt_strasse IS NOT NULL AND objekt_plz IS NOT NULL AND objekt_ort IS NOT NULL))
);

-- =============================================================
-- TABELLE: auftrag_arbeitstyp
-- Many-to-Many: Ein Auftrag kann mehrere Arbeitstypen haben
-- =============================================================

CREATE TABLE auftrag_arbeitstyp (
    auftrag_id      INT NOT NULL REFERENCES auftrag(auftrag_id)    ON DELETE CASCADE,
    arbeitstyp_id   INT NOT NULL REFERENCES arbeitstyp(arbeitstyp_id),
    PRIMARY KEY (auftrag_id, arbeitstyp_id)
);

-- =============================================================
-- TABELLE: rapport
-- Arbeitsrapport des Mitarbeiters nach Ausführung
-- =============================================================

CREATE TABLE rapport (
    rapport_id          SERIAL          PRIMARY KEY,
    auftrag_id          INT             NOT NULL REFERENCES auftrag(auftrag_id) ON DELETE CASCADE,
    mitarbeiter_id      INT             NOT NULL REFERENCES benutzer(benutzer_id),
    freigegeben_von     INT             REFERENCES benutzer(benutzer_id),

    datum               DATE            NOT NULL,
    stunden             NUMERIC(5,2)    NOT NULL,
    arbeitsbeschreibung TEXT            NOT NULL,
    material            TEXT,
    freigabe_datum      DATE,

    erstellt_am         TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_stunden_positiv CHECK (stunden > 0),
    CONSTRAINT chk_stunden_max     CHECK (stunden <= 24)
);

-- =============================================================
-- TABELLE: rechnung
-- Rechnung zum Auftrag (erstellt nach Freigabe)
-- =============================================================

CREATE TABLE rechnung (
    rechnung_id     SERIAL          PRIMARY KEY,
    auftrag_id      INT             NOT NULL UNIQUE REFERENCES auftrag(auftrag_id) ON DELETE CASCADE,
    erstellt_von    INT             NOT NULL REFERENCES benutzer(benutzer_id),

    rechnungsdatum  DATE            NOT NULL DEFAULT CURRENT_DATE,
    betrag          NUMERIC(10,2)   NOT NULL,
    positionen      TEXT,

    erstellt_am     TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_betrag_positiv CHECK (betrag > 0)
);

-- =============================================================
-- INDIZES (für häufige Abfragen)
-- =============================================================

-- Aufträge nach Status filtern (Listenansicht)
CREATE INDEX idx_auftrag_status       ON auftrag(status);

-- Aufträge eines Bereichsleiters
CREATE INDEX idx_auftrag_bereichsleiter ON auftrag(bereichsleiter_id);

-- Aufträge eines Mitarbeiters
CREATE INDEX idx_auftrag_mitarbeiter  ON auftrag(mitarbeiter_id);

-- Aufträge nach Erfassungsdatum sortieren
CREATE INDEX idx_auftrag_datum        ON auftrag(erfassungsdatum DESC);

-- Kunden nach Name suchen
CREATE INDEX idx_kunde_nachname       ON kunde(nachname);

-- Benutzer nach Rolle filtern (Dropdown Mitarbeiter/BL)
CREATE INDEX idx_benutzer_rolle       ON benutzer(rolle);

-- Rapporte eines Auftrags
CREATE INDEX idx_rapport_auftrag      ON rapport(auftrag_id);
