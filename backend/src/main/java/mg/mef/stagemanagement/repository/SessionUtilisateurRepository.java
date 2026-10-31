package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.SessionUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SessionUtilisateurRepository
        extends JpaRepository<SessionUtilisateur, Integer> {

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("update SessionUtilisateur s set s.dateRevocation = :now where s.utilisateur.id = :userId and s.dateRevocation is null")
    int revokeAll(@org.springframework.data.repository.query.Param("userId") Integer userId,
            @org.springframework.data.repository.query.Param("now") LocalDateTime now);

    Optional<SessionUtilisateur>
    findByJetonHashAndDateRevocationIsNullAndDateExpirationAfter(
            String jetonHash,
            LocalDateTime maintenant
    );
}