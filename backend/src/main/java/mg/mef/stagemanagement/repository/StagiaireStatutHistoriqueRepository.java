package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StagiaireStatutHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagiaireStatutHistoriqueRepository extends JpaRepository<StagiaireStatutHistorique, Integer> {
}
