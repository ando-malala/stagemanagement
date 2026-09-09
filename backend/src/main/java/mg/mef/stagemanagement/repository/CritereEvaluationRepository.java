package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.CritereEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CritereEvaluationRepository extends JpaRepository<CritereEvaluation, Integer> {
}
