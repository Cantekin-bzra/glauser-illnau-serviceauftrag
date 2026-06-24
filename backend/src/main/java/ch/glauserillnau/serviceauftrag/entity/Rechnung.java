package ch.glauserillnau.serviceauftrag.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rechnung")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rechnung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rechnung_id")
    private Integer rechnungId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auftrag_id", nullable = false, unique = true)
    private Auftrag auftrag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "erstellt_von", nullable = false)
    private Benutzer erstelltVon;

    @Column(name = "rechnungsdatum", nullable = false)
    private LocalDate rechnungsdatum = LocalDate.now();

    @Column(name = "betrag", nullable = false, precision = 10, scale = 2)
    private BigDecimal betrag;

    @Column(name = "positionen", columnDefinition = "TEXT")
    private String positionen;

    @Column(name = "erstellt_am", nullable = false, updatable = false)
    private LocalDateTime erstelltAm = LocalDateTime.now();
}
