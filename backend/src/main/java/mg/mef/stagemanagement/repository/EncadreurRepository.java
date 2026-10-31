package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Encadreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface EncadreurRepository extends JpaRepository<Encadreur, Integer> {
    interface Charge {
        Integer getEncadreurId();
        Integer getCapaciteMax();
        Long getParticipationsActives();
        Boolean getSurcharge();
    }
    @Query(value = "select encadreur_id as encadreurId, capacite_max as capaciteMax, participations_actives as participationsActives, surcharge from vue_charge_encadreurs where encadreur_id = :id", nativeQuery = true)
    Optional<Charge> findCharge(@org.springframework.data.repository.query.Param("id") Integer id);

    Optional<Encadreur> findByUtilisateur_Id(Integer utilisateurId);

    boolean existsByUtilisateur_Id(Integer utilisateurId);
    
    @Query(
        value = """
            SELECT
                v.encadreur_id,
                v.capacite_max,
                v.participations_actives
            FROM vue_charge_encadreurs v
            WHERE v.surcharge = TRUE
            ORDER BY v.encadreur_id
            """,
        nativeQuery = true
    )
    List<Object[]> findEncadreursSurcharges();

}
