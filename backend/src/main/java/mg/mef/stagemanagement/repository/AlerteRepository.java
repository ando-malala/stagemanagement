package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Alerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Integer> {
}
