package ch.glauserillnau.serviceauftrag.repository;

import ch.glauserillnau.serviceauftrag.entity.Benutzer;
import ch.glauserillnau.serviceauftrag.enums.Rolle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BenutzerRepository – Integrationstests")
class BenutzerRepositoryTest {

    @Autowired BenutzerRepository benutzerRepository;

    @Test
    @DisplayName("Benutzer speichern und per Benutzername laden")
    void save_andFindByBenutzername() {
        benutzerRepository.save(Benutzer.builder()
                .benutzername("bl.test").passwortHash("hash")
                .vorname("Brigitte").nachname("Lüscher")
                .rolle(Rolle.BEREICHSLEITER).aktiv(true).build());

        Optional<Benutzer> result = benutzerRepository.findByBenutzername("bl.test");

        assertThat(result).isPresent();
        assertThat(result.get().getRolle()).isEqualTo(Rolle.BEREICHSLEITER);
        assertThat(result.get().getVorname()).isEqualTo("Brigitte");
    }

    @Test
    @DisplayName("findByBenutzername bei unbekanntem Namen gibt leeres Optional")
    void findByBenutzername_unknown_returnsEmpty() {
        Optional<Benutzer> result = benutzerRepository.findByBenutzername("gibts.nicht");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByRolle gibt nur Mitarbeiter zurück")
    void findByRolle_mitarbeiter() {
        benutzerRepository.save(Benutzer.builder()
                .benutzername("ma.one").passwortHash("h")
                .vorname("Hans").nachname("Eins")
                .rolle(Rolle.MITARBEITER).aktiv(true).build());
        benutzerRepository.save(Benutzer.builder()
                .benutzername("ma.two").passwortHash("h")
                .vorname("Peter").nachname("Zwei")
                .rolle(Rolle.MITARBEITER).aktiv(true).build());
        benutzerRepository.save(Benutzer.builder()
                .benutzername("admin.one").passwortHash("h")
                .vorname("Admin").nachname("Eins")
                .rolle(Rolle.ADMIN).aktiv(true).build());

        List<Benutzer> mitarbeiter = benutzerRepository.findByRolle(Rolle.MITARBEITER);

        assertThat(mitarbeiter).hasSize(2);
        assertThat(mitarbeiter).allMatch(b -> b.getRolle() == Rolle.MITARBEITER);
    }

    @Test
    @DisplayName("findByAktivTrue gibt nur aktive Benutzer zurück")
    void findByAktivTrue_returnsOnlyActive() {
        benutzerRepository.save(Benutzer.builder()
                .benutzername("aktiv.one").passwortHash("h")
                .vorname("Aktiv").nachname("Eins")
                .rolle(Rolle.MITARBEITER).aktiv(true).build());
        benutzerRepository.save(Benutzer.builder()
                .benutzername("inaktiv.one").passwortHash("h")
                .vorname("Inaktiv").nachname("Eins")
                .rolle(Rolle.MITARBEITER).aktiv(false).build());

        List<Benutzer> aktive = benutzerRepository.findByAktivTrue();

        assertThat(aktive).allMatch(Benutzer::isAktiv);
        assertThat(aktive).noneMatch(b -> b.getBenutzername().startsWith("inaktiv"));
    }

    @Test
    @DisplayName("findByRolleAndAktivTrue filtert Rolle und aktiv kombiniert")
    void findByRolleAndAktivTrue() {
        benutzerRepository.save(Benutzer.builder()
                .benutzername("ma.aktiv").passwortHash("h")
                .vorname("MA").nachname("Aktiv")
                .rolle(Rolle.MITARBEITER).aktiv(true).build());
        benutzerRepository.save(Benutzer.builder()
                .benutzername("ma.inaktiv").passwortHash("h")
                .vorname("MA").nachname("Inaktiv")
                .rolle(Rolle.MITARBEITER).aktiv(false).build());
        benutzerRepository.save(Benutzer.builder()
                .benutzername("bl.aktiv").passwortHash("h")
                .vorname("BL").nachname("Aktiv")
                .rolle(Rolle.BEREICHSLEITER).aktiv(true).build());

        List<Benutzer> result = benutzerRepository.findByRolleAndAktivTrue(Rolle.MITARBEITER);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBenutzername()).isEqualTo("ma.aktiv");
    }
}
