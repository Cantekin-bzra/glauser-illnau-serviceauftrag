package ch.glauserillnau.serviceauftrag.repository;

import ch.glauserillnau.serviceauftrag.entity.Arbeitstyp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArbeitstypRepository extends JpaRepository<Arbeitstyp, Integer> {
}
