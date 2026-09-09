package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Stagiaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagiaireRepository extends JpaRepository<Stagiaire, Integer> {
}
