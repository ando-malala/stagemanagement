package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Encadreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EncadreurRepository extends JpaRepository<Encadreur, Integer> {

    Optional<Encadreur> findByUtilisateur_Id(Integer utilisateurId);

    boolean existsByUtilisateur_Id(Integer utilisateurId);
}