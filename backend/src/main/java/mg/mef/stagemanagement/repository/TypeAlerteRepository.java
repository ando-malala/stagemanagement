package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.TypeAlerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeAlerteRepository extends JpaRepository<TypeAlerte, Integer> {

    Optional<TypeAlerte> findByCodeIgnoreCaseAndActifTrue(String code);
}