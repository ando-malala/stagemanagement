package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StructureAccueil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StructureAccueilRepository
        extends JpaRepository<StructureAccueil, Integer> {

    Optional<StructureAccueil> findByDepartement_IdAndLibelleIgnoreCase(
            Integer departementId,
            String libelle
    );

    boolean existsByDepartement_IdAndLibelleIgnoreCase(
            Integer departementId,
            String libelle
    );
}