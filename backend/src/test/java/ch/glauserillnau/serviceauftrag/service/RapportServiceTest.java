package ch.glauserillnau.serviceauftrag.service;

import ch.glauserillnau.serviceauftrag.dto.RapportDTO;
import ch.glauserillnau.serviceauftrag.entity.Auftrag;
import ch.glauserillnau.serviceauftrag.entity.Benutzer;
import ch.glauserillnau.serviceauftrag.entity.Kunde;
import ch.glauserillnau.serviceauftrag.entity.Rapport;
import ch.glauserillnau.serviceauftrag.enums.AuftragStatus;
import ch.glauserillnau.serviceauftrag.enums.Rolle;
import ch.glauserillnau.serviceauftrag.repository.AuftragRepository;
import ch.glauserillnau.serviceauftrag.repository.BenutzerRepository;
import ch.glauserillnau.serviceauftrag.repository.RapportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RapportService – Unit Tests")
class RapportServiceTest {

    @Mock RapportRepository  rapportRepository;
    @Mock AuftragRepository  auftragRepository;
    @Mock BenutzerRepository benutzerRepository;

    @InjectMocks RapportService rapportService;

    private Auftrag  auftragDisponiert;
    private Auftrag  auftragErfasst;
    private Benutzer testMA;
    private Benutzer testBL;

    @BeforeEach
    void setUp() {
        Kunde kunde = Kunde.builder()
                .kundenId(1).vorname("Max").nachname("Muster")
                .telefon("052 000 00 00").strasse("Musterstr. 1").plz("8000").ort("Zürich")
                .build();

        testMA = Benutzer.builder()
                .benutzerId(4).benutzername("ma.mueller").vorname("Hans").nachname("Müller")
                .rolle(Rolle.MITARBEITER).aktiv(true).build();

        testBL = Benutzer.builder()
                .benutzerId(2).benutzername("bl.glauser").vorname("Beat").nachname("Glauser")
                .rolle(Rolle.BEREICHSLEITER).aktiv(true).build();

        auftragDisponiert = Auftrag.builder()
                .auftragId(1).kunde(kunde).titel("Test")
                .beschreibung("Beschreibung").status(AuftragStatus.DISPONIERT)
                .mitarbeiter(testMA).bereichsleiter(testBL).build();

        auftragErfasst = Auftrag.builder()
                .auftragId(2).kunde(kunde).titel("Test2")
                .beschreibung("Beschreibung2").status(AuftragStatus.ERFASST).build();
    }

    @Test
    @DisplayName("Rapport erstellen setzt Auftrag auf AUSGEFUEHRT")
    void erstellen_setsAuftragAusgefuehrt() {
        RapportDTO dto = new RapportDTO();
        dto.setDatum(LocalDate.now());
        dto.setStunden(new BigDecimal("2.5"));
        dto.setArbeitsbeschreibung("Dichtung ersetzt.");

        when(auftragRepository.findById(1)).thenReturn(Optional.of(auftragDisponiert));
        when(benutzerRepository.findById(4)).thenReturn(Optional.of(testMA));
        when(rapportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(auftragRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rapport rapport = rapportService.erstellen(1, 4, dto);

        assertThat(rapport.getArbeitsbeschreibung()).isEqualTo("Dichtung ersetzt.");
        assertThat(rapport.getStunden()).isEqualByComparingTo(new BigDecimal("2.5"));
        assertThat(auftragDisponiert.getStatus()).isEqualTo(AuftragStatus.AUSGEFUEHRT);
        assertThat(auftragDisponiert.getAusfuehrungsdatum()).isEqualTo(dto.getDatum());
    }

    @Test
    @DisplayName("Rapport erstellen bei nicht disponiertem Auftrag wirft Exception")
    void erstellen_auftragNichtDisponiert_throwsException() {
        RapportDTO dto = new RapportDTO();
        dto.setDatum(LocalDate.now());
        dto.setStunden(new BigDecimal("1.0"));
        dto.setArbeitsbeschreibung("Test");

        when(auftragRepository.findById(2)).thenReturn(Optional.of(auftragErfasst));

        assertThatThrownBy(() -> rapportService.erstellen(2, 4, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("disponierte Aufträge");
    }

    @Test
    @DisplayName("Rapport freigeben setzt Freigabedatum und freigegebenVon")
    void freigeben_setsFreigabeDatumAndBL() {
        Rapport rapport = Rapport.builder()
                .rapportId(10).auftrag(auftragDisponiert).mitarbeiter(testMA)
                .datum(LocalDate.now()).stunden(new BigDecimal("1.5"))
                .arbeitsbeschreibung("Arbeit done").build();

        when(rapportRepository.findById(10)).thenReturn(Optional.of(rapport));
        when(benutzerRepository.findById(2)).thenReturn(Optional.of(testBL));
        when(rapportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rapport result = rapportService.freigeben(10, 2);

        assertThat(result.getFreigegebenVon()).isEqualTo(testBL);
        assertThat(result.getFreigabeDatum()).isNotNull();
    }

    @Test
    @DisplayName("Rapport freigeben mit unbekanntem Rapport-ID wirft Exception")
    void freigeben_unknownRapportId_throwsException() {
        when(rapportRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rapportService.freigeben(999, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rapport nicht gefunden");
    }
}
