package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.JournalAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalAuditRepository extends JpaRepository<JournalAudit, Integer> {

    List<JournalAudit> findAllByOrderByDateActionDesc();

    List<JournalAudit> findByUtilisateur_IdOrderByDateActionDesc(
            Integer utilisateurId
    );

    List<JournalAudit> findByEntiteAndEntiteIdOrderByDateActionDesc(
            String entite,
            Integer entiteId
    );
}