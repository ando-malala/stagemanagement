package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Alerte;
import mg.mef.stagemanagement.model.enums.AlerteStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Integer> {

    List<Alerte> findByStatutOrderByDateDetectionDesc(AlerteStatut statut);

    List<Alerte> findAllByOrderByDateDetectionDesc();

    boolean existsByCleDeduplication(String cleDeduplication);

    long countByStatut(AlerteStatut statut);

    Optional<Alerte> findByCleDeduplication(String cleDeduplication);

    List<Alerte> findByTypeAlerte_Code(String code);

    @org.springframework.data.jpa.repository.Query("""
            select a from Alerte a
            left join a.stageParticipant p
            left join a.rapport r
            left join a.soutenance s
            where a.statut = :statut
            and (p.id = :participationId or r.stageParticipant.id = :participationId
                 or s.stageParticipant.id = :participationId)
            """)
    List<Alerte> findByParticipationAndStatut(
            @org.springframework.data.repository.query.Param("participationId") Integer participationId,
            @org.springframework.data.repository.query.Param("statut") AlerteStatut statut);
}
