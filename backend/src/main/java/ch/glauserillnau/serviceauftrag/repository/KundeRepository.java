package ch.glauserillnau.serviceauftrag.repository;

import ch.glauserillnau.serviceauftrag.entity.Kunde;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KundeRepository extends JpaRepository<Kunde, Integer> {
    List<Kunde> findByNachnameContainingIgnoreCase(String nachname);
}
