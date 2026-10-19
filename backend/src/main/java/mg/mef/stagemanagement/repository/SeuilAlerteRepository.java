package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.SeuilAlerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeuilAlerteRepository
        extends JpaRepository<SeuilAlerte, Integer> {

    List<SeuilAlerte> findByTypeAlerte_CodeAndActifTrueOrderByJoursAvantAsc(
            String code
    );
}
