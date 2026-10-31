package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.TransitionStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransitionStatutRepository extends JpaRepository<TransitionStatut, Integer> {
    boolean existsByStatutSource_IdAndStatutCible_IdAndActifTrue(Integer sourceId, Integer cibleId);
}
