package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StageParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

        @Query("""
                SELECT sp
                FROM StageParticipant sp
                JOIN sp.statut s
                WHERE sp.dateFinStage < :dateActuelle
                AND sp.dateCloture IS NULL
                AND s.code NOT IN ('ANNULEE', 'ABANDONNEE')
        """)
        List<StageParticipant> findStagesDepassesNonClotures(
                @Param("dateActuelle") LocalDate dateActuelle
        );

        
        @Query("""
                SELECT sp
                FROM StageParticipant sp
                JOIN sp.statut s
                WHERE sp.dateFinStage BETWEEN :dateActuelle AND :dateLimite
                AND sp.dateCloture IS NULL
                AND s.code IN ('EN_COURS', 'A_CLOTURER')
        """)
        List<StageParticipant> findStagesProchesDeFin(
                @Param("dateActuelle") LocalDate dateActuelle,
                @Param("dateLimite") LocalDate dateLimite
        );

}