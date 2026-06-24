package ch.glauserillnau.serviceauftrag.repository;

import ch.glauserillnau.serviceauftrag.entity.Auftrag;
import ch.glauserillnau.serviceauftrag.entity.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RapportRepository extends JpaRepository<Rapport, Integer> {
    List<Rapport> findByAuftrag(Auftrag auftrag);
}
