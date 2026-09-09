package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.UtilisateurStatutHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurStatutHistoriqueRepository extends JpaRepository<UtilisateurStatutHistorique, Integer> {
}
