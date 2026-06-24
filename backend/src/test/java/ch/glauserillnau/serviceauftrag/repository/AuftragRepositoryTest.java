package ch.glauserillnau.serviceauftrag.repository;

import ch.glauserillnau.serviceauftrag.entity.Auftrag;
import ch.glauserillnau.serviceauftrag.entity.Benutzer;
import ch.glauserillnau.serviceauftrag.entity.Kunde;
import ch.glauserillnau.serviceauftrag.enums.AuftragStatus;
import ch.glauserillnau.serviceauftrag.enums.Rolle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("AuftragRepository – Integrationstests")
class AuftragRepositoryTest {

    @Autowired AuftragRepository  auftragRepository;
    @Autowired KundeRepository    kundeRepository;
    @Autowired BenutzerRepository benutzerRepository;

    private Kunde    kunde;
    private Benutzer mitarbeiter;

    @BeforeEach
    void setUp() {
        kunde = kundeRepository.save(Kunde.builder()
                .vorname("Felix").nachname("Fischer")
                .telefon("052 111 22 33")
                .strasse("Testgasse 1").plz("8400").ort("Winterthur")
                .build());

        mitarbeiter = benutzerRepository.save(Benutzer.builder()
                .benutzername("ma.test").passwortHash("hash")
                .vorname("Marc").nachname("Test").email("ma.test@glauser.ch")
                .rolle(Rolle.MITARBEITER).aktiv(true)
                .build());
    }

    @Test
    @DisplayName("Auftrag speichern und per ID laden")
    void save_andFindById() {
        Auftrag a = auftragRepository.save(Auftrag.builder()
                .kunde(kunde).titel("Rohrbruch").beschreibung("Keller")
                .status(AuftragStatus.ERFASST).build());

        Optional<Auftrag> gefunden = auftragRepository.findById(a.getAuftragId());

        assertThat(gefunden).isPresent();
        assertThat(gefunden.get().getTitel()).isEqualTo("Rohrbruch");
        assertThat(gefunden.get().getStatus()).isEqualTo(AuftragStatus.ERFASST);
    }

    @Test
    @DisplayName("findByStatus gibt nur Aufträge mit passendem Status zurück")
    void findByStatus_returnsMatchingOnly() {
        auftragRepository.save(Auftrag.builder()
                .kunde(kunde).titel("A1").beschreibung("d")
                .status(AuftragStatus.ERFASST).build());
        auftragRepository.save(Auftrag.builder()
                .kunde(kunde).titel("A2").beschreibung("d")
                .status(AuftragStatus.DISPONIERT)
                .mitarbeiter(mitarbeiter).terminGeplant(LocalDate.now()).build());
        auftragRepository.save(Auftrag.builder()
                .kunde(kunde).titel("A3").beschreibung("d")
                .status(AuftragStatus.ERFASST).build());

        List<Auftrag> erfasst = auftragRepository.findByStatus(AuftragStatus.ERFASST);

        assertThat(erfasst).hasSize(2);
        assertThat(erfasst).allMatch(a -> a.getStatus() == AuftragStatus.ERFASST);
    }

    @Test
    @DisplayName("findByMitarbeiter gibt nur Aufträge des Mitarbeiters zurück")
    void findByMitarbeiter_returnsAssignedOnly() {
        auftragRepository.save(Auftrag.builder()
                .kunde(kunde).titel("MA-Auftrag").beschreibung("d")
                .status(AuftragStatus.DISPONIERT)
                .mitarbeiter(mitarbeiter).terminGeplant(LocalDate.now()).build());
        auftragRepository.save(Auftrag.builder()
                .kunde(kunde).titel("Kein MA").beschreibung("d")
                .status(AuftragStatus.ERFASST).build());

        List<Auftrag> result = auftragRepository.findByMitarbeiter(mitarbeiter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitel()).isEqualTo("MA-Auftrag");
    }

    @Test
    @DisplayName("Statusänderung wird persistiert")
    void statusChange_isPersisted() {
        Auftrag a = auftragRepository.save(Auftrag.builder()
                .kunde(kunde).titel("Status-Test").beschreibung("d")
                .status(AuftragStatus.ERFASST).build());

        a.setStatus(AuftragStatus.DISPONIERT);
        a.setMitarbeiter(mitarbeiter);
        a.setTerminGeplant(LocalDate.now());
        auftragRepository.save(a);

        Auftrag geladen = auftragRepository.findById(a.getAuftragId()).orElseThrow();
        assertThat(geladen.getStatus()).isEqualTo(AuftragStatus.DISPONIERT);
        assertThat(geladen.getMitarbeiter().getBenutzerId()).isEqualTo(mitarbeiter.getBenutzerId());
    }

    @Test
    @DisplayName("Auftrag löschen entfernt den Eintrag")
    void delete_removesEntry() {
        Auftrag a = auftragRepository.save(Auftrag.builder()
                .kunde(kunde).titel("Zu löschen").beschreibung("d")
                .status(AuftragStatus.ERFASST).build());
        int id = a.getAuftragId();

        auftragRepository.deleteById(id);

        assertThat(auftragRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Alle Aufträge eines Kunden finden")
    void findByKunde_returnsAllKundeOrders() {
        Kunde andererKunde = kundeRepository.save(Kunde.builder()
                .vorname("Anna").nachname("Anders")
                .telefon("044 000 00 00")
                .strasse("Anderestr. 2").plz("8000").ort("Zürich")
                .build());

        auftragRepository.save(Auftrag.builder()
                .kunde(kunde).titel("K1").beschreibung("d").status(AuftragStatus.ERFASST).build());
        auftragRepository.save(Auftrag.builder()
                .kunde(kunde).titel("K2").beschreibung("d").status(AuftragStatus.ERFASST).build());
        auftragRepository.save(Auftrag.builder()
                .kunde(andererKunde).titel("K3").beschreibung("d").status(AuftragStatus.ERFASST).build());

        List<Auftrag> result = auftragRepository.findByKunde(kunde);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(a -> a.getKunde().getKundenId() == kunde.getKundenId());
    }
}
