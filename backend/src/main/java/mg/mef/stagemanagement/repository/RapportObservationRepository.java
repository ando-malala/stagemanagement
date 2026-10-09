package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.RapportObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RapportObservationRepository
        extends JpaRepository<RapportObservation, Integer> {

    List<RapportObservation> findByRapport_IdOrderByDateCreationAsc(
            Integer rapportId
    );
}