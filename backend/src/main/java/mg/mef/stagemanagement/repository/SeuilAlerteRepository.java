package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.SeuilAlerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeuilAlerteRepository extends JpaRepository<SeuilAlerte, Integer> {
}
