-- =============================================================
-- Glauser Illnau AG – Serviceauftrag-Verwaltung
-- Testdaten: 02_testdaten.sql
-- Zuständig: Sami Bouddat
-- =============================================================

-- =============================================================
-- ARBEITSTYPEN
-- =============================================================

INSERT INTO arbeitstyp (bezeichnung) VALUES
    ('Reparatur'),
    ('Sanitär'),
    ('Heizung'),
    ('Garantie');

-- =============================================================
-- BENUTZER (Passwort-Hash = bcrypt von "Test1234!")
-- =============================================================

INSERT INTO benutzer (benutzername, vorname, nachname, email, passwort_hash, rolle) VALUES
    ('admin.muster',    'Max',      'Muster',       'max.muster@glauser-illnau.ch',     '$2a$10$xLqKzRjlbTqhS1mYkF8zNeKkFbE1Q2aWj3kP5vN9tL7dH4cM0rGue', 'ADMIN'),
    ('bl.glauser',      'Beat',     'Glauser',      'beat.glauser@glauser-illnau.ch',   '$2a$10$xLqKzRjlbTqhS1mYkF8zNeKkFbE1Q2aWj3kP5vN9tL7dH4cM0rGue', 'BEREICHSLEITER'),
    ('bl.huber',        'Sandra',   'Huber',        'sandra.huber@glauser-illnau.ch',   '$2a$10$xLqKzRjlbTqhS1mYkF8zNeKkFbE1Q2aWj3kP5vN9tL7dH4cM0rGue', 'BEREICHSLEITER'),
    ('ma.mueller',      'Hans',     'Müller',       'hans.mueller@glauser-illnau.ch',   '$2a$10$xLqKzRjlbTqhS1mYkF8zNeKkFbE1Q2aWj3kP5vN9tL7dH4cM0rGue', 'MITARBEITER'),
    ('ma.schneider',    'Peter',    'Schneider',    'peter.schneider@glauser-illnau.ch','$2a$10$xLqKzRjlbTqhS1mYkF8zNeKkFbE1Q2aWj3kP5vN9tL7dH4cM0rGue', 'MITARBEITER'),
    ('ma.weber',        'Anna',     'Weber',        'anna.weber@glauser-illnau.ch',     '$2a$10$xLqKzRjlbTqhS1mYkF8zNeKkFbE1Q2aWj3kP5vN9tL7dH4cM0rGue', 'MITARBEITER');

-- =============================================================
-- KUNDEN
-- =============================================================

INSERT INTO kunde (vorname, nachname, firma, telefon, natel, email, strasse, plz, ort) VALUES
    ('Beatrice', 'Brandenberger', NULL,               '052 765 43 21', '079 797 97 97', 'b.brandenberger@gmail.com',     'Obderdorfstrasse 11',  '8484', 'Weisslingen'),
    ('Rudolf',   'Rutishauser',   NULL,               '044 888 11 22', '076 123 45 67', 'r.rutishauser@bluewin.ch',      'Unterdorfstrasse 22',  '8484', 'Weisslingen'),
    ('Stefan',   'Meier',         'Meier Bau AG',     '052 300 10 20', '079 300 10 20', 's.meier@meier-bau.ch',          'Industriestrasse 5',   '8308', 'Illnau'),
    ('Andrea',   'Keller',        NULL,               '052 400 20 30', NULL,            'andrea.keller@hotmail.com',     'Rosenweg 3',           '8307', 'Effretikon'),
    ('Markus',   'Fischer',       NULL,               '052 500 30 40', '078 500 30 40', 'm.fischer@gmx.ch',              'Bahnhofstrasse 14',    '8484', 'Weisslingen'),
    ('Ursula',   'Zimmermann',    'Zimmi GmbH',       '044 600 40 50', '077 600 40 50', 'u.zimmermann@zimmi.ch',         'Zürichstrasse 88',     '8308', 'Illnau'),
    ('Thomas',   'Bauer',         NULL,               '052 700 50 60', NULL,            'thomas.bauer@sunrise.ch',       'Feldstrasse 7',        '8307', 'Effretikon'),
    ('Monika',   'Steiner',       NULL,               '052 800 60 70', '076 800 60 70', 'monika.steiner@icloud.com',     'Kirchgasse 2',         '8484', 'Weisslingen'),
    ('Daniel',   'Wolf',          'Wolf & Partner',   '044 900 70 80', '079 900 70 80', 'd.wolf@wolf-partner.ch',        'Hauptstrasse 55',      '8308', 'Illnau'),
    ('Laura',    'Schmid',        NULL,               '052 100 80 90', NULL,            'laura.schmid@gmail.com',        'Bergstrasse 19',       '8307', 'Effretikon');

-- =============================================================
-- AUFTRÄGE (verschiedene Status)
-- =============================================================

-- Auftrag 1: ERFASST
INSERT INTO auftrag (kunden_id, titel, beschreibung, status, erfassungsdatum, erfassungszeit, terminwunsch, objekt_gleich_kunde)
VALUES (1, 'Wasserhahn Küche tropft', 'Wasserhahn in der Küche tropft/undicht. Dichtung muss ersetzt werden.', 'ERFASST', '2024-09-23', '10:00', 'So schnell wie möglich', TRUE);

INSERT INTO auftrag_arbeitstyp VALUES (1, 1), (1, 2); -- Reparatur, Sanitär

-- Auftrag 2: ERFASST
INSERT INTO auftrag (kunden_id, titel, beschreibung, status, erfassungsdatum, erfassungszeit, terminwunsch, objekt_gleich_kunde)
VALUES (5, 'Dusche läuft nicht ab', 'Abfluss in der Dusche verstopft, Wasser staut sich.', 'ERFASST', '2024-09-24', '08:30', 'Diese Woche', TRUE);

INSERT INTO auftrag_arbeitstyp VALUES (2, 2); -- Sanitär

-- Auftrag 3: DISPONIERT
INSERT INTO auftrag (kunden_id, bereichsleiter_id, mitarbeiter_id, titel, beschreibung, status, erfassungsdatum, erfassungszeit, terminwunsch, termin_geplant, objekt_gleich_kunde)
VALUES (2, 2, 4, 'Heizung startet nicht', 'Heizung startet nicht, keine Wärme im ganzen Haus. Letzter Service vor 3 Jahren.', 'DISPONIERT', '2024-09-21', '09:15', 'Dringend', '2024-09-25', TRUE);

INSERT INTO auftrag_arbeitstyp VALUES (3, 3); -- Heizung

-- Auftrag 4: DISPONIERT
INSERT INTO auftrag (kunden_id, bereichsleiter_id, mitarbeiter_id, titel, beschreibung, status, erfassungsdatum, erfassungszeit, terminwunsch, termin_geplant, objekt_gleich_kunde, verrechnung_abweichend, verrechnung_vorname, verrechnung_nachname, verrechnung_strasse, verrechnung_plz, verrechnung_ort)
VALUES (3, 2, 5, 'WC-Spülung defekt', 'WC-Spülung funktioniert nicht mehr, Griff gebrochen.', 'DISPONIERT', '2024-09-20', '14:00', 'Nächste Woche', '2024-09-26', TRUE, TRUE, 'Stefan', 'Meier', 'Postfach 100', '8308', 'Illnau');

INSERT INTO auftrag_arbeitstyp VALUES (4, 2); -- Sanitär

-- Auftrag 5: DISPONIERT
INSERT INTO auftrag (kunden_id, bereichsleiter_id, mitarbeiter_id, titel, beschreibung, status, erfassungsdatum, erfassungszeit, terminwunsch, termin_geplant, objekt_gleich_kunde)
VALUES (7, 3, 6, 'Boiler wechseln', 'Alter Boiler (30 Jahre) muss ersetzt werden. Warmwasser funktioniert noch knapp.', 'DISPONIERT', '2024-09-19', '11:00', 'Ende Monat', '2024-09-28', TRUE);

INSERT INTO auftrag_arbeitstyp VALUES (5, 2), (5, 3); -- Sanitär, Heizung

-- Auftrag 6: AUSGEFÜHRT
INSERT INTO auftrag (kunden_id, bereichsleiter_id, mitarbeiter_id, titel, beschreibung, status, erfassungsdatum, erfassungszeit, terminwunsch, termin_geplant, ausfuehrungsdatum, objekt_gleich_kunde)
VALUES (4, 2, 4, 'Heizkörper entlüften', 'Heizkörper im OG heizen nicht richtig, müssen entlüftet werden.', 'AUSGEFUEHRT', '2024-09-18', '13:00', 'Bald möglich', '2024-09-22', '2024-09-22', TRUE);

INSERT INTO auftrag_arbeitstyp VALUES (6, 3); -- Heizung

INSERT INTO rapport (auftrag_id, mitarbeiter_id, datum, stunden, arbeitsbeschreibung, material)
VALUES (6, 4, '2024-09-22', 1.5, 'Alle Heizkörper im OG entlüftet. Heizungsanlage auf Druck geprüft, Nachfüllung 0.3 bar.', 'Entlüftungsschlüssel, 5L Heizungswasser');

-- Auftrag 7: AUSGEFÜHRT
INSERT INTO auftrag (kunden_id, bereichsleiter_id, mitarbeiter_id, titel, beschreibung, status, erfassungsdatum, erfassungszeit, terminwunsch, termin_geplant, ausfuehrungsdatum, objekt_gleich_kunde)
VALUES (6, 3, 5, 'Garantiefall Armatur', 'Neue Armatur (Kauf vor 8 Monaten) tropft bereits wieder. Garantiefall prüfen.', 'AUSGEFUEHRT', '2024-09-17', '10:30', 'Flexibel', '2024-09-23', '2024-09-23', TRUE);

INSERT INTO auftrag_arbeitstyp VALUES (7, 1), (7, 4); -- Reparatur, Garantie

INSERT INTO rapport (auftrag_id, mitarbeiter_id, datum, stunden, arbeitsbeschreibung, material)
VALUES (7, 5, '2024-09-23', 0.5, 'Armatur geprüft. Dichtung werksseitig fehlerhaft. Austausch unter Garantie vorgenommen.', 'Ersatzdichtung Hansgrohe Art. 97567 (Garantielieferung)');

-- Auftrag 8: FREIGEGEBEN
INSERT INTO auftrag (kunden_id, bereichsleiter_id, mitarbeiter_id, titel, beschreibung, status, erfassungsdatum, erfassungszeit, terminwunsch, termin_geplant, ausfuehrungsdatum, freigabe_datum, objekt_gleich_kunde)
VALUES (8, 2, 4, 'Rohrbruch Keller', 'Wasserrohr im Keller gebrochen, Wasser läuft aus. Notfall!', 'FREIGEGEBEN', '2024-09-15', '07:00', 'Sofort!', '2024-09-15', '2024-09-15', '2024-09-16', TRUE);

INSERT INTO auftrag_arbeitstyp VALUES (8, 1), (8, 2); -- Reparatur, Sanitär

INSERT INTO rapport (auftrag_id, mitarbeiter_id, freigegeben_von, datum, stunden, arbeitsbeschreibung, material, freigabe_datum)
VALUES (8, 4, 2, '2024-09-15', 3.0, 'Rohrbruch lokalisiert und abgedichtet. Provisorische Reparatur gesetzt. Definitiver Austausch des Rohrstücks (50cm) durchgeführt.', '1x Kupferrohr DN15 50cm, 2x Lötfitting, Lötzinn', '2024-09-16');

-- Auftrag 9: VERRECHNET
INSERT INTO auftrag (kunden_id, bereichsleiter_id, mitarbeiter_id, titel, beschreibung, status, erfassungsdatum, erfassungszeit, terminwunsch, termin_geplant, ausfuehrungsdatum, freigabe_datum, verrechnet_am, objekt_gleich_kunde)
VALUES (9, 3, 5, 'Jahresservice Heizung', 'Jährlicher Servicevertrag Heizungsanlage Wolf. Alle Wartungsarbeiten gemäss Checkliste.', 'VERRECHNET', '2024-09-10', '09:00', 'KW38', '2024-09-16', '2024-09-16', '2024-09-17', '2024-09-18', TRUE);

INSERT INTO auftrag_arbeitstyp VALUES (9, 3); -- Heizung

INSERT INTO rapport (auftrag_id, mitarbeiter_id, freigegeben_von, datum, stunden, arbeitsbeschreibung, material, freigabe_datum)
VALUES (9, 5, 3, '2024-09-16', 2.5, 'Jahresservice komplett durchgeführt: Filter gereinigt, Brenner geprüft, Druck kontrolliert, Abgaswerte gemessen, Protokoll ausgefüllt.', 'Filtereinsatz Art. 4421, 2L Heizöl-Additiv', '2024-09-17');

INSERT INTO rechnung (auftrag_id, erstellt_von, rechnungsdatum, betrag, positionen)
VALUES (9, 1, '2024-09-18', 385.00, 'Arbeitszeit 2.5h à CHF 120.00 = CHF 300.00; Filtereinsatz = CHF 45.00; Additiv = CHF 40.00');

-- Auftrag 10: VERRECHNET
INSERT INTO auftrag (kunden_id, bereichsleiter_id, mitarbeiter_id, titel, beschreibung, status, erfassungsdatum, erfassungszeit, terminwunsch, termin_geplant, ausfuehrungsdatum, freigabe_datum, verrechnet_am, objekt_gleich_kunde, objekt_strasse, objekt_plz, objekt_ort)
VALUES (10, 2, 6, 'Badezimmer Renovation Sanitär', 'Kompletter Sanitärausbau neues Badezimmer: Dusche, WC, Lavabo neu installieren.', 'VERRECHNET', '2024-09-05', '10:00', 'Ab 16.09.', '2024-09-16', '2024-09-19', '2024-09-20', '2024-09-21', FALSE, 'Bergstrasse 19', '8307', 'Effretikon');

INSERT INTO auftrag_arbeitstyp VALUES (10, 2); -- Sanitär

INSERT INTO rapport (auftrag_id, mitarbeiter_id, freigegeben_von, datum, stunden, arbeitsbeschreibung, material, freigabe_datum)
VALUES (10, 6, 2, '2024-09-19', 8.0, 'Vollständiger Sanitärausbau: Dusche inkl. Ablauf, WC-Keramik, Lavabo mit Armatur, alle Zuleitungen und Abflüsse neu verlegt.', 'Dusche Kaldewei, WC Geberit, Lavabo Laufen, Armaturen Grohe, diverse Rohre und Fittings', '2024-09-20');

INSERT INTO rechnung (auftrag_id, erstellt_von, rechnungsdatum, betrag, positionen)
VALUES (10, 1, '2024-09-21', 3240.00, 'Arbeitszeit 8h à CHF 120.00 = CHF 960.00; Material Sanitär = CHF 2280.00');

-- Auftrag 11: ERFASST (Neuauftrag heute)
INSERT INTO auftrag (kunden_id, titel, beschreibung, status, erfassungsdatum, erfassungszeit, terminwunsch, objekt_gleich_kunde)
VALUES (6, 'Thermostat defekt', 'Raumthermostat reagiert nicht mehr, Heizung läuft dauerhaft.', 'ERFASST', '2024-09-25', '11:30', 'Diese oder nächste Woche', TRUE);

INSERT INTO auftrag_arbeitstyp VALUES (11, 3); -- Heizung

-- Auftrag 12: DISPONIERT
INSERT INTO auftrag (kunden_id, bereichsleiter_id, mitarbeiter_id, titel, beschreibung, status, erfassungsdatum, erfassungszeit, terminwunsch, termin_geplant, objekt_gleich_kunde)
VALUES (9, 3, 4, 'Wasserfilter wechseln', 'Wasserfilter der Hauszuleitung muss gemäss Wartungsplan gewechselt werden.', 'DISPONIERT', '2024-09-22', '15:00', 'Ende Monat', '2024-09-30', TRUE);

INSERT INTO auftrag_arbeitstyp VALUES (12, 2); -- Sanitär
