package ch.glauserillnau.serviceauftrag.entity;

import ch.glauserillnau.serviceauftrag.enums.Rolle;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "benutzer")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Benutzer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benutzer_id")
    private Integer benutzerId;

    @Column(name = "benutzername", nullable = false, unique = true, length = 50)
    private String benutzername;

    @Column(name = "vorname", nullable = false, length = 100)
    private String vorname;

    @Column(name = "nachname", nullable = false, length = 100)
    private String nachname;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "passwort_hash", nullable = false, length = 255)
    private String passwortHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "rolle", nullable = false)
    private Rolle rolle;

    @Column(name = "aktiv", nullable = false)
    private boolean aktiv = true;

    @Column(name = "erstellt_am", nullable = false, updatable = false)
    private LocalDateTime erstelltAm = LocalDateTime.now();

    public String getVollname() {
        return vorname + " " + nachname;
    }
}
