package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.TypePieceJointe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypePieceJointeRepository
        extends JpaRepository<TypePieceJointe, Integer> {

    Optional<TypePieceJointe> findByCodeIgnoreCaseAndActifTrue(String code);
}