package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StageParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StageParticipantRepository extends JpaRepository<StageParticipant, Integer> {
}
