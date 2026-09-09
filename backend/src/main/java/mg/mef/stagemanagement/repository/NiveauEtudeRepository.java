package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.NiveauEtude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NiveauEtudeRepository extends JpaRepository<NiveauEtude, Integer> {
}
