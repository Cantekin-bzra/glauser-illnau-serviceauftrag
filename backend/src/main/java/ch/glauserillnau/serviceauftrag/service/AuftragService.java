package ch.glauserillnau.serviceauftrag.service;

import ch.glauserillnau.serviceauftrag.dto.AuftragDTO;
import ch.glauserillnau.serviceauftrag.dto.DispositionDTO;
import ch.glauserillnau.serviceauftrag.entity.Arbeitstyp;
import ch.glauserillnau.serviceauftrag.entity.Auftrag;
import ch.glauserillnau.serviceauftrag.entity.Benutzer;
import ch.glauserillnau.serviceauftrag.entity.Kunde;
import ch.glauserillnau.serviceauftrag.enums.AuftragStatus;
import ch.glauserillnau.serviceauftrag.enums.Rolle;
import ch.glauserillnau.serviceauftrag.repository.ArbeitstypRepository;
import ch.glauserillnau.serviceauftrag.repository.AuftragRepository;
import ch.glauserillnau.serviceauftrag.repository.BenutzerRepository;
import ch.glauserillnau.serviceauftrag.repository.KundeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuftragService {

    private final AuftragRepository auftragRepository;
    private final KundeRepository kundeRepository;
    private final BenutzerRepository benutzerRepository;
    private final ArbeitstypRepository arbeitstypRepository;

    public List<Auftrag> findAll() {
        return auftragRepository.findAllByOrderByErfassungsdatumDesc();
    }

    public List<Auftrag> findByStatus(AuftragStatus status) {
        return auftragRepository.findByStatusOrderByErfassungsdatumDesc(status);
    }

    public Auftrag findById(Integer id) {
        return auftragRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Auftrag nicht gefunden: " + id));
    }

    public List<Benutzer> findAlleMitarbeiter() {
        return benutzerRepository.findByRolleAndAktivTrue(Rolle.MITARBEITER);
    }

    public List<Benutzer> findAlleBereichsleiter() {
        return benutzerRepository.findByRolleAndAktivTrue(Rolle.BEREICHSLEITER);
    }

    public List<Arbeitstyp> findAlleArbeitstypen() {
        return arbeitstypRepository.findAll();
    }

    public List<Kunde> findAlleKunden() {
        return kundeRepository.findAll();
    }

    @Transactional
    public Auftrag erfassen(AuftragDTO dto) {
        Kunde kunde = kundeRepository.findById(dto.getKundenId())
                .orElseThrow(() -> new IllegalArgumentException("Kunde nicht gefunden"));

        Set<Arbeitstyp> arbeitstypen = new HashSet<>();
        if (dto.getArbeitstypIds() != null) {
            arbeitstypen.addAll(arbeitstypRepository.findAllById(dto.getArbeitstypIds()));
        }

        Auftrag auftrag = Auftrag.builder()
                .kunde(kunde)
                .titel(dto.getTitel())
                .beschreibung(dto.getBeschreibung())
                .terminwunsch(dto.getTerminwunsch())
                .status(AuftragStatus.ERFASST)
                .objektGleichKunde(dto.isObjektGleichKunde())
                .objektStrasse(dto.getObjektStrasse())
                .objektPlz(dto.getObjektPlz())
                .objektOrt(dto.getObjektOrt())
                .verrechnungAbweichend(dto.isVerrechnungAbweichend())
                .verrechnungVorname(dto.getVerrechnungVorname())
                .verrechnungNachname(dto.getVerrechnungNachname())
                .verrechnungStrasse(dto.getVerrechnungStrasse())
                .verrechnungPlz(dto.getVerrechnungPlz())
                .verrechnungOrt(dto.getVerrechnungOrt())
                .arbeitstypen(arbeitstypen)
                .build();

        return auftragRepository.save(auftrag);
    }

    @Transactional
    public Auftrag disponieren(Integer auftragId, DispositionDTO dto, Integer bereichsleiterId) {
        Auftrag auftrag = findById(auftragId);
        pruefeStatusUebergang(auftrag, AuftragStatus.ERFASST, AuftragStatus.DISPONIERT);

        Benutzer mitarbeiter = benutzerRepository.findById(dto.getMitarbeiterId())
                .orElseThrow(() -> new IllegalArgumentException("Mitarbeiter nicht gefunden"));
        Benutzer bereichsleiter = benutzerRepository.findById(bereichsleiterId)
                .orElseThrow(() -> new IllegalArgumentException("Bereichsleiter nicht gefunden"));

        auftrag.setMitarbeiter(mitarbeiter);
        auftrag.setBereichsleiter(bereichsleiter);
        auftrag.setTerminGeplant(dto.getTerminGeplant());
        auftrag.setStatus(AuftragStatus.DISPONIERT);

        return auftragRepository.save(auftrag);
    }

    @Transactional
    public Auftrag alsAusgefuehrtMarkieren(Integer auftragId) {
        Auftrag auftrag = findById(auftragId);
        pruefeStatusUebergang(auftrag, AuftragStatus.DISPONIERT, AuftragStatus.AUSGEFUEHRT);

        auftrag.setAusfuehrungsdatum(LocalDate.now());
        auftrag.setStatus(AuftragStatus.AUSGEFUEHRT);

        return auftragRepository.save(auftrag);
    }

    @Transactional
    public Auftrag freigeben(Integer auftragId) {
        Auftrag auftrag = findById(auftragId);
        pruefeStatusUebergang(auftrag, AuftragStatus.AUSGEFUEHRT, AuftragStatus.FREIGEGEBEN);

        auftrag.setFreigabeDatum(LocalDate.now());
        auftrag.setStatus(AuftragStatus.FREIGEGEBEN);

        return auftragRepository.save(auftrag);
    }

    @Transactional
    public Auftrag alsVerrechnetMarkieren(Integer auftragId) {
        Auftrag auftrag = findById(auftragId);
        pruefeStatusUebergang(auftrag, AuftragStatus.FREIGEGEBEN, AuftragStatus.VERRECHNET);

        auftrag.setVerrechnetAm(LocalDate.now());
        auftrag.setStatus(AuftragStatus.VERRECHNET);

        return auftragRepository.save(auftrag);
    }

    // Verhindert ungültige Statusübergänge
    private void pruefeStatusUebergang(Auftrag auftrag, AuftragStatus erwartet, AuftragStatus ziel) {
        if (auftrag.getStatus() != erwartet) {
            throw new IllegalStateException(
                "Ungültiger Statusübergang: Auftrag ist '" + auftrag.getStatus() +
                "', erwartet '" + erwartet + "' für Übergang zu '" + ziel + "'"
            );
        }
    }
}
