package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FiliereRepository
        extends JpaRepository<Filiere, Integer> {

    Optional<Filiere> findByLibelleIgnoreCase(String libelle);

    boolean existsByLibelleIgnoreCase(String libelle);
}