package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.EvaluationCritere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationCritereRepository extends JpaRepository<EvaluationCritere, Integer> {
}
