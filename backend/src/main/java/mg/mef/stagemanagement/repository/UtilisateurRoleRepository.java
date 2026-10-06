package mg.mef.stagemanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.mef.stagemanagement.model.UtilisateurRole;

@Repository
public interface UtilisateurRoleRepository
        extends JpaRepository<UtilisateurRole, Integer> {

    List<UtilisateurRole> findByUtilisateur_Id(Integer utilisateurId);

    boolean existsByUtilisateur_IdAndRole_Id(
            Integer utilisateurId,
            Integer roleId
    );

    Optional<UtilisateurRole> findByUtilisateur_IdAndRole_Code(
            Integer utilisateurId,
            String roleCode
    );
}