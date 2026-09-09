package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.RapportActiviteStatutHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RapportActiviteStatutHistoriqueRepository extends JpaRepository<RapportActiviteStatutHistorique, Integer> {
}
