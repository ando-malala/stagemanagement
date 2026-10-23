
package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StageParticipantEvenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageParticipantEvenementRepository
        extends JpaRepository<StageParticipantEvenement, Integer> {

    List<StageParticipantEvenement>
            findByStageParticipant_IdOrderByDateEvenementAscIdAsc(
                    Integer stageParticipantId
            );
}
