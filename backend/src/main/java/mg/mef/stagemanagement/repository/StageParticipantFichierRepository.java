package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StageParticipantFichier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageParticipantFichierRepository
        extends JpaRepository<StageParticipantFichier, Integer> {

    List<StageParticipantFichier> findByStageParticipant_Id(
            Integer stageParticipantId
    );

    List<StageParticipantFichier> findByFichier_Id(Integer fichierId);

    boolean existsByStageParticipant_IdAndTypePieceJointe_Id(
            Integer stageParticipantId,
            Integer typePieceJointeId
    );
}