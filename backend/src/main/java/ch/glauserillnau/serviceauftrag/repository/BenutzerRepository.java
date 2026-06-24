package ch.glauserillnau.serviceauftrag.repository;

import ch.glauserillnau.serviceauftrag.entity.Benutzer;
import ch.glauserillnau.serviceauftrag.enums.Rolle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BenutzerRepository extends JpaRepository<Benutzer, Integer> {
    List<Benutzer> findByRolleAndAktivTrue(Rolle rolle);
    List<Benutzer> findByRolle(Rolle rolle);
    List<Benutzer> findByAktivTrue();
    Optional<Benutzer> findByBenutzername(String benutzername);
}
