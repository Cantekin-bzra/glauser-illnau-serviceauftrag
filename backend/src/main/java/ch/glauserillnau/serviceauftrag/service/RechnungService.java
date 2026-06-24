package ch.glauserillnau.serviceauftrag.service;

import ch.glauserillnau.serviceauftrag.entity.Auftrag;
import ch.glauserillnau.serviceauftrag.entity.Benutzer;
import ch.glauserillnau.serviceauftrag.entity.Rechnung;
import ch.glauserillnau.serviceauftrag.enums.AuftragStatus;
import ch.glauserillnau.serviceauftrag.repository.AuftragRepository;
import ch.glauserillnau.serviceauftrag.repository.BenutzerRepository;
import ch.glauserillnau.serviceauftrag.repository.RechnungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RechnungService {

    private final RechnungRepository rechnungRepository;
    private final AuftragRepository auftragRepository;
    private final BenutzerRepository benutzerRepository;

    public Optional<Rechnung> findByAuftragId(Integer auftragId) {
        Auftrag auftrag = auftragRepository.findById(auftragId)
                .orElseThrow(() -> new IllegalArgumentException("Auftrag nicht gefunden"));
        return rechnungRepository.findByAuftrag(auftrag);
    }

    @Transactional
    public Rechnung erstellen(Integer auftragId, Integer erstelltVonId, BigDecimal betrag, String positionen) {
        Auftrag auftrag = auftragRepository.findById(auftragId)
                .orElseThrow(() -> new IllegalArgumentException("Auftrag nicht gefunden"));
        Benutzer erstelltVon = benutzerRepository.findById(erstelltVonId)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden"));

        if (auftrag.getStatus() != AuftragStatus.FREIGEGEBEN) {
            throw new IllegalStateException("Rechnung kann nur für freigegebene Aufträge erstellt werden");
        }

        Rechnung rechnung = Rechnung.builder()
                .auftrag(auftrag)
                .erstelltVon(erstelltVon)
                .betrag(betrag)
                .positionen(positionen)
                .build();

        return rechnungRepository.save(rechnung);
    }
}
