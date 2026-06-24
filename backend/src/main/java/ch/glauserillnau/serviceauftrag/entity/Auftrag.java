package ch.glauserillnau.serviceauftrag.entity;

import ch.glauserillnau.serviceauftrag.enums.AuftragStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "auftrag")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Auftrag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auftrag_id")
    private Integer auftragId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kunden_id", nullable = false)
    private Kunde kunde;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bereichsleiter_id")
    private Benutzer bereichsleiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mitarbeiter_id")
    private Benutzer mitarbeiter;

    @Column(name = "titel", nullable = false, length = 200)
    private String titel;

    @Column(name = "beschreibung", nullable = false, columnDefinition = "TEXT")
    private String beschreibung;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AuftragStatus status = AuftragStatus.ERFASST;

    // Zeitstempel
    @Column(name = "erfassungsdatum", nullable = false)
    private LocalDate erfassungsdatum = LocalDate.now();

    @Column(name = "erfassungszeit", nullable = false)
    private LocalTime erfassungszeit = LocalTime.now();

    @Column(name = "terminwunsch", length = 200)
    private String terminwunsch;

    @Column(name = "termin_geplant")
    private LocalDate terminGeplant;

    @Column(name = "ausfuehrungsdatum")
    private LocalDate ausfuehrungsdatum;

    @Column(name = "freigabe_datum")
    private LocalDate freigabeDatum;

    @Column(name = "verrechnet_am")
    private LocalDate verrechnetAm;

    // Objektadresse
    @Column(name = "objekt_gleich_kunde", nullable = false)
    private boolean objektGleichKunde = true;

    @Column(name = "objekt_strasse", length = 150)
    private String objektStrasse;

    @Column(name = "objekt_plz", length = 10)
    private String objektPlz;

    @Column(name = "objekt_ort", length = 100)
    private String objektOrt;

    // Verrechnungsadresse
    @Column(name = "verrechnung_abweichend", nullable = false)
    private boolean verrechnungAbweichend = false;

    @Column(name = "verrechnung_vorname", length = 100)
    private String verrechnungVorname;

    @Column(name = "verrechnung_nachname", length = 100)
    private String verrechnungNachname;

    @Column(name = "verrechnung_strasse", length = 150)
    private String verrechnungStrasse;

    @Column(name = "verrechnung_plz", length = 10)
    private String verrechnungPlz;

    @Column(name = "verrechnung_ort", length = 100)
    private String verrechnungOrt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "auftrag_arbeitstyp",
        joinColumns = @JoinColumn(name = "auftrag_id"),
        inverseJoinColumns = @JoinColumn(name = "arbeitstyp_id")
    )
    @Builder.Default
    private Set<Arbeitstyp> arbeitstypen = new HashSet<>();

    @Column(name = "erstellt_am", nullable = false, updatable = false)
    private LocalDateTime erstelltAm = LocalDateTime.now();

    @Column(name = "aktualisiert_am", nullable = false)
    private LocalDateTime aktualisiertAm = LocalDateTime.now();

    @PreUpdate
    void onUpdate() {
        this.aktualisiertAm = LocalDateTime.now();
    }

    public String getObjektAdresse() {
        if (objektGleichKunde) return kunde.getAdresse();
        return objektStrasse + ", " + objektPlz + " " + objektOrt;
    }
}
