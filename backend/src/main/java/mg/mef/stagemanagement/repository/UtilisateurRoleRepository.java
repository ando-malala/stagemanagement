package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.UtilisateurRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRoleRepository extends JpaRepository<UtilisateurRole, Integer> {
}
