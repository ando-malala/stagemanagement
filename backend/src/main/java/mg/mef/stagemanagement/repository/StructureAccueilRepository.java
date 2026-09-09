package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StructureAccueil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StructureAccueilRepository extends JpaRepository<StructureAccueil, Integer> {
}
