package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StagiaireFichier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagiaireFichierRepository extends JpaRepository<StagiaireFichier, Integer> {
}
