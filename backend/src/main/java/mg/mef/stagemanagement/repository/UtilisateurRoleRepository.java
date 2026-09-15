package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.UtilisateurRole;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.mef.stagemanagement.model.Utilisateur;

@Repository
public interface UtilisateurRoleRepository extends JpaRepository<UtilisateurRole, Integer> {
    List<UtilisateurRole> findByUtilisateur_Id(Integer utilisateurId);
}
