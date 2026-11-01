package mg.mef.stagemanagement.service;

import java.util.List;
import mg.mef.stagemanagement.dto.stagiaire.*;
import mg.mef.stagemanagement.dto.stage.StageParticipantResponse;
import mg.mef.stagemanagement.dto.rapport.*;
import mg.mef.stagemanagement.dto.fichier.FichierResponse;
import mg.mef.stagemanagement.dto.evaluation.EvaluationFinaleResponse;
import mg.mef.stagemanagement.dto.evaluation.EvaluationCritereResponse;
import mg.mef.stagemanagement.dto.soutenance.SoutenanceResponse;
import mg.mef.stagemanagement.repository.EvaluationFinaleRepository;
import mg.mef.stagemanagement.repository.SoutenanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DossierSyntheseService {
    public record Rapport(RapportActiviteResponse rapport, List<RapportObservationResponse> observations) {}
    public record Participation(StageParticipantResponse participation, List<Rapport> rapports,
            List<SuiviParticipationService.Periode> periodes, List<FichierResponse> fichiers,
            List<DocumentCatalogueService.DocumentObligatoire> documentsObligatoires,
            List<SuiviParticipationService.Historique> historique, List<SuiviParticipationService.Evenement> evenements,
            EvaluationFinaleResponse evaluation, List<EvaluationCritereResponse> notes, List<SoutenanceResponse> soutenances) {}
    public record Dossier(StagiaireResponse stagiaire, List<StagiaireAcademiqueResponse> academiques,
            List<FichierResponse> fichiers, List<Participation> participations) {}
    private final DossierAccessService access;
    private final StagiaireGestionService stagiaires;
    private final StageGestionService stages;
    private final RapportActiviteGestionService rapports;
    private final FichierGestionService fichiers;
    private final SuiviParticipationService suivi;
    private final DocumentCatalogueService documents;
    private final EvaluationGestionService evaluations;
    private final EvaluationFinaleRepository evaluationRepository;
    private final SoutenanceGestionService soutenances;
    private final SoutenanceRepository soutenanceRepository;

    public DossierSyntheseService(DossierAccessService access, StagiaireGestionService stagiaires, StageGestionService stages,
            RapportActiviteGestionService rapports, FichierGestionService fichiers, SuiviParticipationService suivi,
            DocumentCatalogueService documents, EvaluationGestionService evaluations, EvaluationFinaleRepository evaluationRepository,
            SoutenanceGestionService soutenances, SoutenanceRepository soutenanceRepository) {
        this.access = access; this.stagiaires = stagiaires; this.stages = stages; this.rapports = rapports;
        this.fichiers = fichiers; this.suivi = suivi; this.documents = documents; this.evaluations = evaluations;
        this.evaluationRepository = evaluationRepository; this.soutenances = soutenances; this.soutenanceRepository = soutenanceRepository;
    }
    public Dossier dossier(Integer id) {
        access.trainee(id, false);
        return new Dossier(stagiaires.trouverStagiaire(id), stagiaires.listerAcademiques(id), fichiers.listerFichiersStagiaire(id),
                stages.listerParticipationsStagiaire(id).stream().map(p -> participation(p.getId())).toList());
    }
    public Participation participation(Integer id) {
        access.participation(id, false);
        // Preserve the existing role boundary: administrative evaluations/defenses are not exposed to trainees.
        var evaluation = access.traineeOnly() ? null : evaluationRepository.findByStageParticipant_Id(id)
                .map(e -> evaluations.trouverEvaluation(e.getId())).orElse(null);
        var defenses = access.traineeOnly() ? List.<SoutenanceResponse>of() : soutenanceRepository.findByStageParticipant_Id(id)
                .stream().map(s -> soutenances.trouverSoutenance(s.getId())).toList();
        return new Participation(stages.trouverParticipation(id), rapports.listerRapportsParParticipation(id).stream()
                .map(r -> new Rapport(r, rapports.listerObservations(r.getId()))).toList(), suivi.periodes(id),
                fichiers.listerFichiersParticipation(id), documents.obligatoires(id), suivi.historiques(id), suivi.evenements(id), evaluation,
                evaluation == null ? List.of() : evaluations.listerNotes(evaluation.getId()), defenses);
    }
}
