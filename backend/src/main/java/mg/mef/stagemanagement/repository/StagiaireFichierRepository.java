package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StagiaireFichier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StagiaireFichierRepository
        extends JpaRepository<StagiaireFichier, Integer> {

    List<StagiaireFichier> findByStagiaire_Id(Integer stagiaireId);

    List<StagiaireFichier> findByFichier_Id(Integer fichierId);

    boolean existsByStagiaire_IdAndTypePieceJointe_Id(
            Integer stagiaireId,
            Integer typePieceJointeId
    );
}