package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.ParametresSuivi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametresSuiviRepository extends JpaRepository<ParametresSuivi, Short> {
}
