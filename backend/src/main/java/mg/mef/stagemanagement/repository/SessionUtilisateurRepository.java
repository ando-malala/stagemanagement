package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.SessionUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SessionUtilisateurRepository
        extends JpaRepository<SessionUtilisateur, Integer> {

    Optional<SessionUtilisateur>
    findByJetonHashAndDateRevocationIsNullAndDateExpirationAfter(
            String jetonHash,
            LocalDateTime maintenant
    );
}