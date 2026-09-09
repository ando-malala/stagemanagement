package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Fichier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FichierRepository extends JpaRepository<Fichier, Integer> {
}
