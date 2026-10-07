package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface StatutRepository extends JpaRepository<Statut, Integer> {

    @Query(value = """
        SELECT s.*
        FROM statuts s
        JOIN types_statut ts ON ts.id = s.type_statut_id
        WHERE ts.code = 'UTILISATEUR'
          AND s.code = 'ACTIF'
          AND s.actif = TRUE
          AND ts.actif = TRUE
        """, nativeQuery = true)
    Optional<Statut> findStatutActifUtilisateur();

    @Query(value = """
        SELECT s.*
        FROM statuts s
        JOIN types_statut ts ON ts.id = s.type_statut_id
        WHERE ts.code = 'UTILISATEUR'
          AND s.code = :code
          AND s.actif = TRUE
          AND ts.actif = TRUE
        """, nativeQuery = true)
    Optional<Statut> findStatutUtilisateurByCode(
            @Param("code") String code
    );

    @Query(value = """
        SELECT s.*
        FROM statuts s
        JOIN types_statut ts ON ts.id = s.type_statut_id
        WHERE ts.code = :typeCode
          AND s.code = :statutCode
          AND s.actif = TRUE
          AND ts.actif = TRUE
        """, nativeQuery = true)
    Optional<Statut> findStatutByTypeCodeAndStatutCode(
            @Param("typeCode") String typeCode,
            @Param("statutCode") String statutCode
    );
}