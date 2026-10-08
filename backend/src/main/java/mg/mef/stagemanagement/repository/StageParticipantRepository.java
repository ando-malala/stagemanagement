package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StageParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StageParticipantRepository
        extends JpaRepository<StageParticipant, Integer> {

    List<StageParticipant> findByStage_Id(Integer stageId);

    List<StageParticipant> findByEncadreur_Id(Integer encadreurId);

    List<StageParticipant> findByStagiaireAcademique_Stagiaire_Id(
            Integer stagiaireId
    );

    boolean existsByStage_IdAndStagiaireAcademique_IdAndDateDebutStage(
            Integer stageId,
            Integer stagiaireAcademiqueId,
            LocalDate dateDebutStage
    );
}