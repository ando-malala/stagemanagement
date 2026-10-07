package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.MoisStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoisStageRepository
        extends JpaRepository<MoisStage, Integer> {

    Optional<MoisStage> findByLibelleIgnoreCase(String libelle);

    boolean existsByLibelleIgnoreCase(String libelle);
}