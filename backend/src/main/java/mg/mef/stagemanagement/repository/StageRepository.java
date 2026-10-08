package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StageRepository extends JpaRepository<Stage, Integer> {

    Optional<Stage> findByLibelleIgnoreCaseAndStructureAccueil_Id(
            String libelle,
            Integer structureAccueilId
    );

    boolean existsByLibelleIgnoreCaseAndStructureAccueil_Id(
            String libelle,
            Integer structureAccueilId
    );
}