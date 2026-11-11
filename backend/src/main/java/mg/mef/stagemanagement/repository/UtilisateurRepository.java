package mg.mef.stagemanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mg.mef.stagemanagement.model.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Utilisateur u where u.id = :id")
    Optional<Utilisateur> findForUpdate(@org.springframework.data.repository.query.Param("id") Integer id);

    @Query("""
        select u from Utilisateur u
        where u.statut.code = 'ACTIF' and u.statut.actif = true
        and exists (select r.id from UtilisateurRole r where r.utilisateur = u and r.role.code = 'STAGIAIRE' and r.role.actif = true)
        and not exists (select t.id from Stagiaire t where t.utilisateur = u and t.id <> :stagiaireId)
        order by u.identifiant
        """)
    List<Utilisateur> findEligibleTraineeAccounts(@org.springframework.data.repository.query.Param("stagiaireId") Integer stagiaireId);

    @Query("""
        select u from Utilisateur u
        where u.statut.code = 'ACTIF' and u.statut.actif = true
        and exists (select r.id from UtilisateurRole r where r.utilisateur = u and r.role.code = 'ENCADREUR' and r.role.actif = true)
        and not exists (select e.id from Encadreur e where e.utilisateur = u and e.id <> :encadreurId)
        order by u.identifiant
        """)
    List<Utilisateur> findEligibleMentorAccounts(@org.springframework.data.repository.query.Param("encadreurId") Integer encadreurId);
    Optional<Utilisateur> findByIdentifiant(String identifiant);
    @Query("""
        SELECT u
        FROM Utilisateur u
        JOIN FETCH u.statut
    """)
    List<Utilisateur> findAllAvecStatut();
    
    boolean existsByIdentifiant(String identifiant);
}
