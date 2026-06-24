package ch.glauserillnau.serviceauftrag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AuftragDTO {

    @NotNull(message = "Kunden-ID ist erforderlich")
    private Integer kundenId;

    @NotBlank(message = "Titel ist erforderlich")
    private String titel;

    @NotBlank(message = "Beschreibung ist erforderlich")
    private String beschreibung;

    private String terminwunsch;
    private List<Integer> arbeitstypIds;

    // Objektadresse
    private boolean objektGleichKunde = true;
    private String objektStrasse;
    private String objektPlz;
    private String objektOrt;

    // Verrechnungsadresse
    private boolean verrechnungAbweichend = false;
    private String verrechnungVorname;
    private String verrechnungNachname;
    private String verrechnungStrasse;
    private String verrechnungPlz;
    private String verrechnungOrt;
}
