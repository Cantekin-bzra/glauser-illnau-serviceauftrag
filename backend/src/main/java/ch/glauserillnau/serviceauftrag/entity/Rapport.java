package ch.glauserillnau.serviceauftrag.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rapport")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rapport_id")
    private Integer rapportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auftrag_id", nullable = false)
    private Auftrag auftrag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mitarbeiter_id", nullable = false)
    private Benutzer mitarbeiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freigegeben_von")
    private Benutzer freigegebenVon;

    @Column(name = "datum", nullable = false)
    private LocalDate datum;

    @Column(name = "stunden", nullable = false, precision = 5, scale = 2)
    private BigDecimal stunden;

    @Column(name = "arbeitsbeschreibung", nullable = false, columnDefinition = "TEXT")
    private String arbeitsbeschreibung;

    @Column(name = "material", columnDefinition = "TEXT")
    private String material;

    @Column(name = "freigabe_datum")
    private LocalDate freigabeDatum;

    @Column(name = "erstellt_am", nullable = false, updatable = false)
    private LocalDateTime erstelltAm = LocalDateTime.now();
}
