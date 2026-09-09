package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.TypePieceJointe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypePieceJointeRepository extends JpaRepository<TypePieceJointe, Integer> {
}
