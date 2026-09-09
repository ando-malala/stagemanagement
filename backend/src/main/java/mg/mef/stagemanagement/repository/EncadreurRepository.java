package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Encadreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncadreurRepository extends JpaRepository<Encadreur, Integer> {
}
