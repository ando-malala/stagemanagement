package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StageParticipantStatutHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StageParticipantStatutHistoriqueRepository extends JpaRepository<StageParticipantStatutHistorique, Integer> {
    java.util.List<StageParticipantStatutHistorique> findByStageParticipant_IdOrderByDateModificationAscIdAsc(Integer id);
}
