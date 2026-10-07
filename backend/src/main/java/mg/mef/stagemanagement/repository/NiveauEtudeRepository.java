package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.NiveauEtude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NiveauEtudeRepository
        extends JpaRepository<NiveauEtude, Integer> {

    Optional<NiveauEtude> findByLibelleIgnoreCase(String libelle);

    boolean existsByLibelleIgnoreCase(String libelle);
}