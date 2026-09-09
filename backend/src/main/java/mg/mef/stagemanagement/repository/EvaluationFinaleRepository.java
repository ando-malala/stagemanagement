package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.EvaluationFinale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationFinaleRepository extends JpaRepository<EvaluationFinale, Integer> {
}
