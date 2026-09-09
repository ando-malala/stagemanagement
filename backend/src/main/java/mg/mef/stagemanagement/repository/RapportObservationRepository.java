package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.RapportObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RapportObservationRepository extends JpaRepository<RapportObservation, Integer> {
}
