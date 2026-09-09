package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.RapportActivite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RapportActiviteRepository extends JpaRepository<RapportActivite, Integer> {
}
