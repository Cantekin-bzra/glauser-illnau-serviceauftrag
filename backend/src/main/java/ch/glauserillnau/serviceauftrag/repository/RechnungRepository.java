package ch.glauserillnau.serviceauftrag.repository;

import ch.glauserillnau.serviceauftrag.entity.Auftrag;
import ch.glauserillnau.serviceauftrag.entity.Rechnung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RechnungRepository extends JpaRepository<Rechnung, Integer> {
    Optional<Rechnung> findByAuftrag(Auftrag auftrag);
}
