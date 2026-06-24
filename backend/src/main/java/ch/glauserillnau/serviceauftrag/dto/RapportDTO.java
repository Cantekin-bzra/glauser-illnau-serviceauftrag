package ch.glauserillnau.serviceauftrag.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RapportDTO {

    @NotNull(message = "Ausführungsdatum ist erforderlich")
    private LocalDate datum;

    @NotNull(message = "Arbeitsstunden sind erforderlich")
    @DecimalMin(value = "0.5", message = "Mindestens 0.5 Stunden")
    private BigDecimal stunden;

    @NotBlank(message = "Arbeitsbeschreibung ist erforderlich")
    private String arbeitsbeschreibung;

    private String material;
}
