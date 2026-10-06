package mg.mef.stagemanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.mef.stagemanagement.model.ResponsableDepartement;

@Repository
public interface ResponsableDepartementRepository
        extends JpaRepository<ResponsableDepartement, Integer> {

    List<ResponsableDepartement> findByUtilisateur_Id(Integer utilisateurId);

    boolean existsByUtilisateur_IdAndDepartement_Id(
            Integer utilisateurId,
            Integer departementId
    );

    Optional<ResponsableDepartement> findByUtilisateur_IdAndDepartement_Id(
            Integer utilisateurId,
            Integer departementId
    );
}