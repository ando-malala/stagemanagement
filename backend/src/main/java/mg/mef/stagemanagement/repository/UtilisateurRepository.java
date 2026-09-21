package mg.mef.stagemanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mg.mef.stagemanagement.model.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByIdentifiant(String identifiant);
    @Query("""
        SELECT u
        FROM Utilisateur u
        JOIN FETCH u.statut
    """)
    List<Utilisateur> findAllAvecStatut();
    
    boolean existsByIdentifiant(String identifiant);
}
