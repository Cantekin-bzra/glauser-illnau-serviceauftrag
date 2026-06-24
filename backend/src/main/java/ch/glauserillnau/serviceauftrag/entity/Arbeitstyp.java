package ch.glauserillnau.serviceauftrag.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "arbeitstyp")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Arbeitstyp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "arbeitstyp_id")
    private Integer arbeitstypId;

    @Column(name = "bezeichnung", nullable = false, unique = true, length = 50)
    private String bezeichnung;
}
