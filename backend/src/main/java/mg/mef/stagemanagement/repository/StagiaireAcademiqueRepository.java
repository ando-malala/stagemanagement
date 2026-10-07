package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StagiaireAcademique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StagiaireAcademiqueRepository
        extends JpaRepository<StagiaireAcademique, Integer> {

    List<StagiaireAcademique> findByStagiaire_Id(Integer stagiaireId);

    boolean existsByStagiaire_IdAndNiveauEtude_IdAndFiliere_IdAndEtablissement_IdAndAnneeEtudes(
            Integer stagiaireId,
            Integer niveauEtudeId,
            Integer filiereId,
            Integer etablissementId,
            String anneeEtudes
    );
}