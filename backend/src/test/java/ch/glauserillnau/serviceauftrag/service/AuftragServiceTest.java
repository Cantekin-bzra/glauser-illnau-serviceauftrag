package ch.glauserillnau.serviceauftrag.service;

import ch.glauserillnau.serviceauftrag.dto.AuftragDTO;
import ch.glauserillnau.serviceauftrag.dto.DispositionDTO;
import ch.glauserillnau.serviceauftrag.entity.Arbeitstyp;
import ch.glauserillnau.serviceauftrag.entity.Auftrag;
import ch.glauserillnau.serviceauftrag.entity.Benutzer;
import ch.glauserillnau.serviceauftrag.entity.Kunde;
import ch.glauserillnau.serviceauftrag.enums.AuftragStatus;
import ch.glauserillnau.serviceauftrag.enums.Rolle;
import ch.glauserillnau.serviceauftrag.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuftragService – Unit Tests")
class AuftragServiceTest {

    @Mock AuftragRepository     auftragRepository;
    @Mock KundeRepository       kundeRepository;
    @Mock BenutzerRepository    benutzerRepository;
    @Mock ArbeitstypRepository  arbeitstypRepository;

    @InjectMocks AuftragService auftragService;

    private Kunde     testKunde;
    private Benutzer  testBL;
    private Benutzer  testMA;
    private Auftrag   auftragErfasst;
    private Auftrag   auftragDisponiert;
    private Auftrag   auftragAusgefuehrt;
    private Auftrag   auftragFreigegeben;

    @BeforeEach
    void setUp() {
        testKunde = Kunde.builder()
                .kundenId(1).vorname("Beatrice").nachname("Brandenberger")
                .telefon("052 765 43 21").strasse("Obderdorfstrasse 11").plz("8484").ort("Weisslingen")
                .build();

        testBL = Benutzer.builder()
                .benutzerId(2).benutzername("bl.glauser").vorname("Beat").nachname("Glauser")
                .rolle(Rolle.BEREICHSLEITER).aktiv(true).build();

        testMA = Benutzer.builder()
                .benutzerId(4).benutzername("ma.mueller").vorname("Hans").nachname("Müller")
                .rolle(Rolle.MITARBEITER).aktiv(true).build();

        auftragErfasst = Auftrag.builder()
                .auftragId(1).kunde(testKunde).titel("Wasserhahn tropft")
                .beschreibung("Dichtung defekt").status(AuftragStatus.ERFASST).build();

        auftragDisponiert = Auftrag.builder()
                .auftragId(2).kunde(testKunde).titel("Heizung defekt")
                .beschreibung("Startet nicht").status(AuftragStatus.DISPONIERT)
                .mitarbeiter(testMA).bereichsleiter(testBL).terminGeplant(LocalDate.now()).build();

        auftragAusgefuehrt = Auftrag.builder()
                .auftragId(3).kunde(testKunde).titel("WC-Spülung defekt")
                .beschreibung("Griff kaputt").status(AuftragStatus.AUSGEFUEHRT)
                .mitarbeiter(testMA).bereichsleiter(testBL)
                .ausfuehrungsdatum(LocalDate.now()).build();

        auftragFreigegeben = Auftrag.builder()
                .auftragId(4).kunde(testKunde).titel("Rohrbruch")
                .beschreibung("Keller").status(AuftragStatus.FREIGEGEBEN)
                .mitarbeiter(testMA).bereichsleiter(testBL)
                .ausfuehrungsdatum(LocalDate.now()).freigabeDatum(LocalDate.now()).build();
    }

    // ── erfassen ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Auftrag erfassen setzt Status ERFASST")
    void erfassen_setsStatusErfasst() {
        AuftragDTO dto = new AuftragDTO();
        dto.setKundenId(1);
        dto.setTitel("Test");
        dto.setBeschreibung("Beschreibung");

        when(kundeRepository.findById(1)).thenReturn(Optional.of(testKunde));
        when(arbeitstypRepository.findAllById(any())).thenReturn(List.of());
        when(auftragRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Auftrag result = auftragService.erfassen(dto);

        assertThat(result.getStatus()).isEqualTo(AuftragStatus.ERFASST);
        assertThat(result.getKunde()).isEqualTo(testKunde);
        verify(auftragRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Auftrag erfassen mit unbekanntem Kunden wirft Exception")
    void erfassen_unknownKunde_throwsException() {
        AuftragDTO dto = new AuftragDTO();
        dto.setKundenId(99);

        when(kundeRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auftragService.erfassen(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kunde nicht gefunden");
    }

    // ── disponieren ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Disponieren wechselt Status zu DISPONIERT")
    void disponieren_setsStatusDisponiert() {
        DispositionDTO dto = new DispositionDTO();
        dto.setMitarbeiterId(4);
        dto.setTerminGeplant(LocalDate.now().plusDays(2));

        when(auftragRepository.findById(1)).thenReturn(Optional.of(auftragErfasst));
        when(benutzerRepository.findById(4)).thenReturn(Optional.of(testMA));
        when(benutzerRepository.findById(2)).thenReturn(Optional.of(testBL));
        when(auftragRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Auftrag result = auftragService.disponieren(1, dto, 2);

        assertThat(result.getStatus()).isEqualTo(AuftragStatus.DISPONIERT);
        assertThat(result.getMitarbeiter()).isEqualTo(testMA);
        assertThat(result.getBereichsleiter()).isEqualTo(testBL);
        assertThat(result.getTerminGeplant()).isEqualTo(dto.getTerminGeplant());
    }

    @Test
    @DisplayName("Disponieren bei falschem Status wirft IllegalStateException")
    void disponieren_wrongStatus_throwsException() {
        DispositionDTO dto = new DispositionDTO();
        dto.setMitarbeiterId(4);
        dto.setTerminGeplant(LocalDate.now());

        when(auftragRepository.findById(2)).thenReturn(Optional.of(auftragDisponiert));

        assertThatThrownBy(() -> auftragService.disponieren(2, dto, 2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ungültiger Statusübergang");
    }

    // ── alsAusgefuehrtMarkieren ───────────────────────────────────────────────

    @Test
    @DisplayName("Als ausgeführt markieren wechselt Status zu AUSGEFUEHRT")
    void ausgefuehrt_setsStatusAusgefuehrt() {
        when(auftragRepository.findById(2)).thenReturn(Optional.of(auftragDisponiert));
        when(auftragRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Auftrag result = auftragService.alsAusgefuehrtMarkieren(2);

        assertThat(result.getStatus()).isEqualTo(AuftragStatus.AUSGEFUEHRT);
        assertThat(result.getAusfuehrungsdatum()).isNotNull();
    }

    @Test
    @DisplayName("Als ausgeführt markieren bei ERFASST wirft Exception")
    void ausgefuehrt_fromErfasst_throwsException() {
        when(auftragRepository.findById(1)).thenReturn(Optional.of(auftragErfasst));

        assertThatThrownBy(() -> auftragService.alsAusgefuehrtMarkieren(1))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── freigeben ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Freigeben wechselt Status zu FREIGEGEBEN")
    void freigeben_setsStatusFreigegeben() {
        when(auftragRepository.findById(3)).thenReturn(Optional.of(auftragAusgefuehrt));
        when(auftragRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Auftrag result = auftragService.freigeben(3);

        assertThat(result.getStatus()).isEqualTo(AuftragStatus.FREIGEGEBEN);
        assertThat(result.getFreigabeDatum()).isNotNull();
    }

    @Test
    @DisplayName("Freigeben bei DISPONIERT wirft Exception (Schritt überspringen)")
    void freigeben_fromDisponiert_throwsException() {
        when(auftragRepository.findById(2)).thenReturn(Optional.of(auftragDisponiert));

        assertThatThrownBy(() -> auftragService.freigeben(2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ungültiger Statusübergang");
    }

    // ── alsVerrechnetMarkieren ────────────────────────────────────────────────

    @Test
    @DisplayName("Als verrechnet markieren wechselt Status zu VERRECHNET")
    void verrechnet_setsStatusVerrechnet() {
        when(auftragRepository.findById(4)).thenReturn(Optional.of(auftragFreigegeben));
        when(auftragRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Auftrag result = auftragService.alsVerrechnetMarkieren(4);

        assertThat(result.getStatus()).isEqualTo(AuftragStatus.VERRECHNET);
        assertThat(result.getVerrechnetAm()).isNotNull();
    }

    @Test
    @DisplayName("Als verrechnet markieren bei ERFASST wirft Exception")
    void verrechnet_fromErfasst_throwsException() {
        when(auftragRepository.findById(1)).thenReturn(Optional.of(auftragErfasst));

        assertThatThrownBy(() -> auftragService.alsVerrechnetMarkieren(1))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── Vollständiger Workflow ────────────────────────────────────────────────

    @Test
    @DisplayName("Vollständiger Statusübergang ERFASST → VERRECHNET")
    void vollstaendigerWorkflow() {
        Auftrag a = Auftrag.builder()
                .auftragId(10).kunde(testKunde).titel("Workflow-Test")
                .beschreibung("Test").status(AuftragStatus.ERFASST).build();

        DispositionDTO dto = new DispositionDTO();
        dto.setMitarbeiterId(4);
        dto.setTerminGeplant(LocalDate.now().plusDays(1));

        // Schritt 1: ERFASST → DISPONIERT
        when(auftragRepository.findById(10)).thenReturn(Optional.of(a));
        when(benutzerRepository.findById(4)).thenReturn(Optional.of(testMA));
        when(benutzerRepository.findById(2)).thenReturn(Optional.of(testBL));
        when(auftragRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        auftragService.disponieren(10, dto, 2);
        assertThat(a.getStatus()).isEqualTo(AuftragStatus.DISPONIERT);

        // Schritt 2: DISPONIERT → AUSGEFUEHRT
        auftragService.alsAusgefuehrtMarkieren(10);
        assertThat(a.getStatus()).isEqualTo(AuftragStatus.AUSGEFUEHRT);

        // Schritt 3: AUSGEFUEHRT → FREIGEGEBEN
        auftragService.freigeben(10);
        assertThat(a.getStatus()).isEqualTo(AuftragStatus.FREIGEGEBEN);

        // Schritt 4: FREIGEGEBEN → VERRECHNET
        auftragService.alsVerrechnetMarkieren(10);
        assertThat(a.getStatus()).isEqualTo(AuftragStatus.VERRECHNET);
    }
}
