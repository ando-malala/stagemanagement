package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StageParticipantFichier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StageParticipantFichierRepository extends JpaRepository<StageParticipantFichier, Integer> {
}
