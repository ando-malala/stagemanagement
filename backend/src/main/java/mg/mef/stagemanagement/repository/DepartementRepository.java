package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartementRepository
        extends JpaRepository<Departement, Integer> {

    Optional<Departement> findByLibelleIgnoreCase(String libelle);

    boolean existsByLibelleIgnoreCase(String libelle);
}