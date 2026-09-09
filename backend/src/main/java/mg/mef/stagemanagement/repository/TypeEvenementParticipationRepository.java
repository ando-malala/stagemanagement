package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.TypeEvenementParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeEvenementParticipationRepository extends JpaRepository<TypeEvenementParticipation, Integer> {
}
