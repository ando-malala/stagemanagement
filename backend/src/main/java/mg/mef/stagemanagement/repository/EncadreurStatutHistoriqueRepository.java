package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.EncadreurStatutHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncadreurStatutHistoriqueRepository extends JpaRepository<EncadreurStatutHistorique, Integer> {
}
