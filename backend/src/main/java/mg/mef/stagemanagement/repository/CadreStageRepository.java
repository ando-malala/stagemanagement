package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.CadreStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CadreStageRepository extends JpaRepository<CadreStage, Integer> {
}
