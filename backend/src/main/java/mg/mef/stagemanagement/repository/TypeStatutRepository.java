package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.TypeStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeStatutRepository extends JpaRepository<TypeStatut, Integer> {
}
