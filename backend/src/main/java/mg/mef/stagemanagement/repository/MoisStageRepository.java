package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.MoisStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoisStageRepository extends JpaRepository<MoisStage, Integer> {
}
