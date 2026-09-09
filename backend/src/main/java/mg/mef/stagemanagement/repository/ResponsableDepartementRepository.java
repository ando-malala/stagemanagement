package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.ResponsableDepartement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsableDepartementRepository extends JpaRepository<ResponsableDepartement, Integer> {
}
