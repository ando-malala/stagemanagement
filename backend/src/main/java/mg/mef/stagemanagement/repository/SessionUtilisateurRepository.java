package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.SessionUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionUtilisateurRepository extends JpaRepository<SessionUtilisateur, Integer> {
}
