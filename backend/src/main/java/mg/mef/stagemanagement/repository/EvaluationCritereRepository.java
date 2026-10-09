package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.EvaluationCritere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationCritereRepository
        extends JpaRepository<EvaluationCritere, Integer> {

    List<EvaluationCritere> findByEvaluation_Id(Integer evaluationId);

    Optional<EvaluationCritere> findByEvaluation_IdAndCritere_Id(
            Integer evaluationId,
            Integer critereId
    );

    boolean existsByEvaluation_IdAndCritere_Id(
            Integer evaluationId,
            Integer critereId
    );
}