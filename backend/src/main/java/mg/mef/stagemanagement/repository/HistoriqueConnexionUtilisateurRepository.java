package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.HistoriqueConnexionUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoriqueConnexionUtilisateurRepository extends JpaRepository<HistoriqueConnexionUtilisateur, Integer> {
}
