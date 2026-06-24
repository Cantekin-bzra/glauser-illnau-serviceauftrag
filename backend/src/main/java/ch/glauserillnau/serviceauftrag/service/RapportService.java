package ch.glauserillnau.serviceauftrag.service;

import ch.glauserillnau.serviceauftrag.dto.RapportDTO;
import ch.glauserillnau.serviceauftrag.entity.Auftrag;
import ch.glauserillnau.serviceauftrag.entity.Benutzer;
import ch.glauserillnau.serviceauftrag.entity.Rapport;
import ch.glauserillnau.serviceauftrag.enums.AuftragStatus;
import ch.glauserillnau.serviceauftrag.repository.AuftragRepository;
import ch.glauserillnau.serviceauftrag.repository.BenutzerRepository;
import ch.glauserillnau.serviceauftrag.repository.RapportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RapportService {

    private final RapportRepository rapportRepository;
    private final AuftragRepository auftragRepository;
    private final BenutzerRepository benutzerRepository;

    public List<Rapport> findByAuftragId(Integer auftragId) {
        Auftrag auftrag = auftragRepository.findById(auftragId)
                .orElseThrow(() -> new IllegalArgumentException("Auftrag nicht gefunden: " + auftragId));
        return rapportRepository.findByAuftrag(auftrag);
    }

    @Transactional
    public Rapport erstellen(Integer auftragId, Integer mitarbeiterId, RapportDTO dto) {
        Auftrag auftrag = auftragRepository.findById(auftragId)
                .orElseThrow(() -> new IllegalArgumentException("Auftrag nicht gefunden"));
        Benutzer mitarbeiter = benutzerRepository.findById(mitarbeiterId)
                .orElseThrow(() -> new IllegalArgumentException("Mitarbeiter nicht gefunden"));

        if (auftrag.getStatus() != AuftragStatus.DISPONIERT) {
            throw new IllegalStateException("Rapport kann nur für disponierte Aufträge erfasst werden");
        }

        Rapport rapport = Rapport.builder()
                .auftrag(auftrag)
                .mitarbeiter(mitarbeiter)
                .datum(dto.getDatum())
                .stunden(dto.getStunden())
                .arbeitsbeschreibung(dto.getArbeitsbeschreibung())
                .material(dto.getMaterial())
                .build();

        rapportRepository.save(rapport);

        auftrag.setAusfuehrungsdatum(dto.getDatum());
        auftrag.setStatus(AuftragStatus.AUSGEFUEHRT);
        auftragRepository.save(auftrag);

        return rapport;
    }

    @Transactional
    public Rapport freigeben(Integer rapportId, Integer bereichsleiterId) {
        Rapport rapport = rapportRepository.findById(rapportId)
                .orElseThrow(() -> new IllegalArgumentException("Rapport nicht gefunden"));
        Benutzer bereichsleiter = benutzerRepository.findById(bereichsleiterId)
                .orElseThrow(() -> new IllegalArgumentException("Bereichsleiter nicht gefunden"));

        rapport.setFreigegebenVon(bereichsleiter);
        rapport.setFreigabeDatum(LocalDate.now());

        return rapportRepository.save(rapport);
    }
}
