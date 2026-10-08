package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.TypeEvenementParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeEvenementParticipationRepository
        extends JpaRepository<TypeEvenementParticipation, Integer> {

    Optional<TypeEvenementParticipation> findByCode(String code);
}