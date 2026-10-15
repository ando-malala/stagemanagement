package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Alerte;
import mg.mef.stagemanagement.model.enums.AlerteStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Integer> {

    List<Alerte> findByStatutOrderByDateDetectionDesc(AlerteStatut statut);

    List<Alerte> findAllByOrderByDateDetectionDesc();

    boolean existsByCleDeduplication(String cleDeduplication);

    long countByStatut(AlerteStatut statut);

    Optional<Alerte> findByCleDeduplication(String cleDeduplication);

    List<Alerte> findByTypeAlerte_Code(String code);
}