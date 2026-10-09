package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.EvaluationFinale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationFinaleRepository
        extends JpaRepository<EvaluationFinale, Integer> {

    Optional<EvaluationFinale> findByStageParticipant_Id(Integer stageParticipantId);

    boolean existsByStageParticipant_Id(Integer stageParticipantId);
}