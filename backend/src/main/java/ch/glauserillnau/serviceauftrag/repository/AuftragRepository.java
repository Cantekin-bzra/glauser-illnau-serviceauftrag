package ch.glauserillnau.serviceauftrag.repository;

import ch.glauserillnau.serviceauftrag.entity.Auftrag;
import ch.glauserillnau.serviceauftrag.entity.Benutzer;
import ch.glauserillnau.serviceauftrag.enums.AuftragStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuftragRepository extends JpaRepository<Auftrag, Integer> {
    List<Auftrag> findByStatusOrderByErfassungsdatumDesc(AuftragStatus status);
    List<Auftrag> findAllByOrderByErfassungsdatumDesc();
    List<Auftrag> findByBereichsleiter(Benutzer bereichsleiter);
    List<Auftrag> findByMitarbeiter(Benutzer mitarbeiter);
}
