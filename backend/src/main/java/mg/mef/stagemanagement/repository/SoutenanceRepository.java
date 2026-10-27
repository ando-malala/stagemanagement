package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Soutenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

@Repository
public interface SoutenanceRepository extends JpaRepository<Soutenance, Integer> {

    Optional<Soutenance> findByStageParticipant_Id(Integer stageParticipantId);

    boolean existsByStageParticipant_Id(Integer stageParticipantId);

    
    @Query(
        value = """
            SELECT
                v.soutenance_id,
                v.stage_participant_id,
                v.date_soutenance,
                v.jours_restants,
                v.niveau_alerte
            FROM vue_soutenances_prochaines v
            WHERE v.niveau_alerte IN (
                'INFORMATION',
                'AVERTISSEMENT',
                'URGENT'
            )
            ORDER BY v.date_soutenance
            """,
        nativeQuery = true
    )
    List<Object[]> findSoutenancesProches();

}