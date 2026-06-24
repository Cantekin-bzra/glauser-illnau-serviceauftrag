package ch.glauserillnau.serviceauftrag.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kunde")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Kunde {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kunden_id")
    private Integer kundenId;

    @Column(name = "vorname", nullable = false, length = 100)
    private String vorname;

    @Column(name = "nachname", nullable = false, length = 100)
    private String nachname;

    @Column(name = "firma", length = 150)
    private String firma;

    @Column(name = "telefon", nullable = false, length = 30)
    private String telefon;

    @Column(name = "natel", length = 30)
    private String natel;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "strasse", nullable = false, length = 150)
    private String strasse;

    @Column(name = "plz", nullable = false, length = 10)
    private String plz;

    @Column(name = "ort", nullable = false, length = 100)
    private String ort;

    @Column(name = "erstellt_am", nullable = false, updatable = false)
    private LocalDateTime erstelltAm = LocalDateTime.now();

    public String getVollname() {
        return vorname + " " + nachname;
    }

    public String getAdresse() {
        return strasse + ", " + plz + " " + ort;
    }
}
