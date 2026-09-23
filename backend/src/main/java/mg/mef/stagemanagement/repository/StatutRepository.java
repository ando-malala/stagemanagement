package mg.mef.stagemanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mg.mef.stagemanagement.model.Statut;

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
}
