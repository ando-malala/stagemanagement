package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.DepartementStatutHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartementStatutHistoriqueRepository extends JpaRepository<DepartementStatutHistorique, Integer> {
}
