# Datenbank-Setup – Lokales PostgreSQL

**Zuständig:** Sami Bouddat  
**Schritt:** 14

---

## 1. PostgreSQL installieren

**Windows:** PostgreSQL-Installer von der offiziellen Seite herunterladen und ausführen.  
Beim Installer:
- Port: `5432` (Standard)
- Superuser: `postgres`
- Passwort: selbst wählen und sicher notieren

---

## 2. Datenbank erstellen

Nach der Installation in der **SQL Shell (psql)** oder **pgAdmin**:

```sql
CREATE DATABASE serviceauftrag;
CREATE USER sa_user WITH PASSWORD 'dein_passwort';
GRANT ALL PRIVILEGES ON DATABASE serviceauftrag TO sa_user;
```

---

## 3. Schema und Testdaten einspielen

In der psql-Konsole:

```bash
psql -U sa_user -d serviceauftrag -f sql/01_schema.sql
psql -U sa_user -d serviceauftrag -f sql/02_testdaten.sql
```

Oder in pgAdmin: SQL-Dateien öffnen und ausführen.

---

## 4. Spring Boot konfigurieren

In `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/serviceauftrag
spring.datasource.username=sa_user
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=false

spring.thymeleaf.cache=false
```

Das Passwort wird als **Umgebungsvariable** gesetzt – nie im Code hardcoden:

```bash
# Windows PowerShell
$env:DB_PASSWORD = "dein_passwort"

# Windows dauerhaft (Systemsteuerung > Umgebungsvariablen)
# Variable: DB_PASSWORD
# Wert: dein_passwort
```

---

## 5. Verbindung testen

Spring Boot starten und im Log prüfen:

```
HikariPool-1 - Start completed.
```

Kein Fehler = Verbindung erfolgreich.

---

## 6. Wichtige Hinweise

| Regel | Grund |
|---|---|
| Passwort **nie** in Git einchecken | `.gitignore` schützt `.env` und `application-local.properties` |
| Jedes Teammitglied setzt `DB_PASSWORD` lokal | Keine geteilten Credentials |
| Schema via `01_schema.sql` neu einspielen bei Änderungen | `ddl-auto=validate` prüft nur, erstellt nichts |
