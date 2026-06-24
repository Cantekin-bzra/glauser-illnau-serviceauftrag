package ch.glauserillnau.serviceauftrag.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DispositionDTO {

    @NotNull(message = "Mitarbeiter muss zugewiesen werden")
    private Integer mitarbeiterId;

    @NotNull(message = "Geplanter Termin ist erforderlich")
    private LocalDate terminGeplant;

    private String bemerkung;
}
