package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.RapportActivite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RapportActiviteRepository
        extends JpaRepository<RapportActivite, Integer> {

    List<RapportActivite> findByStageParticipant_Id(Integer stageParticipantId);

    boolean existsByStageParticipant_IdAndDebutSemaineAndFinSemaine(
            Integer stageParticipantId,
            LocalDate debutSemaine,
            LocalDate finSemaine
    );
}