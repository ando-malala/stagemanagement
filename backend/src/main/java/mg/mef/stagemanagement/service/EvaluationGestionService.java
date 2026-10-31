package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.evaluation.EvaluationCritereRequest;
import mg.mef.stagemanagement.dto.evaluation.EvaluationCritereResponse;
import mg.mef.stagemanagement.dto.evaluation.EvaluationFinaleRequest;
import mg.mef.stagemanagement.dto.evaluation.EvaluationFinaleResponse;
import mg.mef.stagemanagement.model.CritereEvaluation;
import mg.mef.stagemanagement.model.Encadreur;
import mg.mef.stagemanagement.model.EvaluationCritere;
import mg.mef.stagemanagement.model.EvaluationFinale;
import mg.mef.stagemanagement.model.StageParticipant;
import mg.mef.stagemanagement.model.Stagiaire;
import mg.mef.stagemanagement.model.StagiaireAcademique;
import mg.mef.stagemanagement.repository.CritereEvaluationRepository;
import mg.mef.stagemanagement.repository.EncadreurRepository;
import mg.mef.stagemanagement.repository.EvaluationCritereRepository;
import mg.mef.stagemanagement.repository.EvaluationFinaleRepository;
import mg.mef.stagemanagement.repository.StageParticipantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EvaluationGestionService {
    private final DossierAccessService access;

    private final EvaluationFinaleRepository evaluationFinaleRepository;
    private final EvaluationCritereRepository evaluationCritereRepository;
    private final CritereEvaluationRepository critereEvaluationRepository;
    private final StageParticipantRepository stageParticipantRepository;
    private final EncadreurRepository encadreurRepository;
    private final JournalAuditService journalAuditService;

    public EvaluationGestionService(
            EvaluationFinaleRepository evaluationFinaleRepository,
            EvaluationCritereRepository evaluationCritereRepository,
            CritereEvaluationRepository critereEvaluationRepository,
            StageParticipantRepository stageParticipantRepository,
            EncadreurRepository encadreurRepository,
            JournalAuditService journalAuditService
    , DossierAccessService access) {
        this.access = access;
        this.evaluationFinaleRepository = evaluationFinaleRepository;
        this.evaluationCritereRepository = evaluationCritereRepository;
        this.critereEvaluationRepository = critereEvaluationRepository;
        this.stageParticipantRepository = stageParticipantRepository;
        this.encadreurRepository = encadreurRepository;
        this.journalAuditService = journalAuditService;
    }

    public List<EvaluationFinaleResponse> listerEvaluations() {
        return evaluationFinaleRepository.findAll()
                .stream().filter(access::readEvaluation)
                .map(this::toEvaluationResponse)
                .toList();
    }

    public EvaluationFinaleResponse trouverEvaluation(Integer id) {
        access.evaluation(id, false);
        EvaluationFinale evaluation = evaluationFinaleRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Évaluation introuvable"
                        )
                );

        return toEvaluationResponse(evaluation);
    }

    public EvaluationFinaleResponse trouverEvaluationParParticipation(
            Integer stageParticipantId
    ) {
        access.participation(stageParticipantId, false);
        EvaluationFinale evaluation = evaluationFinaleRepository
                .findByStageParticipant_Id(stageParticipantId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Évaluation introuvable pour cette participation"
                        )
                );

        return toEvaluationResponse(evaluation);
    }

    @Transactional(readOnly = false)
    public EvaluationFinaleResponse creerEvaluation(
            EvaluationFinaleRequest request
    ) {
        if (request != null) access.evaluator(request.getStageParticipantId(), request.getEncadreurId());
        verifierEvaluationRequest(request);

        if (evaluationFinaleRepository.existsByStageParticipant_Id(
                request.getStageParticipantId()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Une évaluation existe déjà pour cette participation"
            );
        }

        StageParticipant participant = stageParticipantRepository
                .findById(request.getStageParticipantId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Participation introuvable"
                        )
                );

        Encadreur encadreur = encadreurRepository
                .findById(request.getEncadreurId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Encadreur introuvable"
                        )
                );

        EvaluationFinale evaluation = new EvaluationFinale();
        evaluation.setStageParticipant(participant);
        evaluation.setEncadreur(encadreur);
        evaluation.setAppreciationGlobale(
                nettoyerTexteOptionnel(request.getAppreciationGlobale())
        );
        evaluation.setObservationsFinales(
                nettoyerTexteOptionnel(request.getObservationsFinales())
        );
        evaluation.setEstFinalisee(false);
        evaluation.setDateEvaluation(LocalDateTime.now());

        EvaluationFinale enregistree =
                evaluationFinaleRepository.save(evaluation);

        return toEvaluationResponse(enregistree);
    }

    @Transactional(readOnly = false)
    public EvaluationFinaleResponse modifierEvaluation(
            Integer id,
            EvaluationFinaleRequest request
    ) {
        access.evaluation(id, true); if (request != null) access.evaluator(request.getStageParticipantId(), request.getEncadreurId());
        verifierEvaluationRequest(request);

        EvaluationFinale evaluation = evaluationFinaleRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Évaluation introuvable"
                        )
                );

        verifierNonFinalisee(evaluation);

        StageParticipant participant = stageParticipantRepository
                .findById(request.getStageParticipantId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Participation introuvable"
                        )
                );

        Encadreur encadreur = encadreurRepository
                .findById(request.getEncadreurId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Encadreur introuvable"
                        )
                );

        evaluation.setStageParticipant(participant);
        evaluation.setEncadreur(encadreur);
        evaluation.setAppreciationGlobale(
                nettoyerTexteOptionnel(request.getAppreciationGlobale())
        );
        evaluation.setObservationsFinales(
                nettoyerTexteOptionnel(request.getObservationsFinales())
        );

        EvaluationFinale enregistree =
                evaluationFinaleRepository.save(evaluation);

        return toEvaluationResponse(enregistree);
    }

    public List<EvaluationCritereResponse> listerNotes(Integer evaluationId) {
        access.evaluation(evaluationId, false);
        if (!evaluationFinaleRepository.existsById(evaluationId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Évaluation introuvable"
            );
        }

        return evaluationCritereRepository.findByEvaluation_Id(evaluationId)
                .stream()
                .map(this::toCritereResponse)
                .toList();
    }

    public List<mg.mef.stagemanagement.dto.evaluation.CritereEvaluationResponse> listerCriteresActifs() {
        return critereEvaluationRepository.findByActifTrueOrderByOrdreAsc()
                .stream()
                .map(c -> new mg.mef.stagemanagement.dto.evaluation.CritereEvaluationResponse(
                        c.getId(),
                        c.getCode(),
                        c.getLibelle(),
                        c.getDescription(),
                        c.getCoefficient(),
                        c.getObligatoire(),
                        c.getOrdre()
                ))
                .toList();
    }

    @Transactional(readOnly = false)
    public EvaluationCritereResponse noterCritere(
            Integer evaluationId,
            EvaluationCritereRequest request
    ) {
        access.evaluation(evaluationId, true);
        if (request == null || request.getCritereId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le critère est obligatoire"
            );
        }

        EvaluationFinale evaluation = evaluationFinaleRepository
                .findById(evaluationId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Évaluation introuvable"
                        )
                );

        verifierNonFinalisee(evaluation);

        CritereEvaluation critere = critereEvaluationRepository
                .findById(request.getCritereId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Critère introuvable"
                        )
                );

        if (!Boolean.TRUE.equals(critere.getActif())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ce critère n'est pas actif"
            );
        }

        verifierNote(request.getNote());

        EvaluationCritere evaluationCritere = evaluationCritereRepository
                .findByEvaluation_IdAndCritere_Id(
                        evaluation.getId(),
                        critere.getId()
                )
                .orElseGet(EvaluationCritere::new);

        evaluationCritere.setEvaluation(evaluation);
        evaluationCritere.setCritere(critere);
        evaluationCritere.setNote(request.getNote());
        evaluationCritere.setCommentaire(
                nettoyerTexteOptionnel(request.getCommentaire())
        );

        EvaluationCritere enregistree =
                evaluationCritereRepository.save(evaluationCritere);

        return toCritereResponse(enregistree);
    }

    @Transactional(readOnly = false)
    public EvaluationFinaleResponse finaliserEvaluation(Integer evaluationId) {
        access.evaluation(evaluationId, true);
        EvaluationFinale evaluation = evaluationFinaleRepository
                .findById(evaluationId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Évaluation introuvable"
                        )
                );

        verifierNonFinalisee(evaluation);

        List<CritereEvaluation> criteresObligatoires =
                critereEvaluationRepository.findByActifTrueOrderByOrdreAsc()
                        .stream()
                        .filter(c -> Boolean.TRUE.equals(c.getObligatoire()))
                        .toList();

        for (CritereEvaluation critere : criteresObligatoires) {
            boolean noteExiste =
                    evaluationCritereRepository.existsByEvaluation_IdAndCritere_Id(
                            evaluation.getId(),
                            critere.getId()
                    );

            if (!noteExiste) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Tous les critères obligatoires doivent être notés avant finalisation"
                );
            }
        }

        evaluation.setEstFinalisee(true);
        evaluation.setDateFinalisation(LocalDateTime.now());

        EvaluationFinale enregistree =
                evaluationFinaleRepository.save(evaluation);
                
        journalAuditService.enregistrer(
                null,
                "FINALISER_EVALUATION",
                "EvaluationFinale",
                enregistree.getId(),
                java.util.Map.of(
                        "estFinalisee", false
                ),
                java.util.Map.of(
                        "estFinalisee", true
                )
        );

        return toEvaluationResponse(enregistree);
    }

    private EvaluationFinaleResponse toEvaluationResponse(
            EvaluationFinale evaluation
    ) {
        StageParticipant participant = evaluation.getStageParticipant();
        Encadreur encadreur = evaluation.getEncadreur();

        String stagiaireNom = null;

        if (participant != null && participant.getStagiaireAcademique() != null) {
            StagiaireAcademique academique = participant.getStagiaireAcademique();
            Stagiaire stagiaire = academique.getStagiaire();

            if (stagiaire != null) {
                stagiaireNom = stagiaire.getNom() + " " + stagiaire.getPrenoms();
            }
        }

        String encadreurNom = encadreur != null
                ? encadreur.getNom() + " " + encadreur.getPrenoms()
                : null;

        return new EvaluationFinaleResponse(
                evaluation.getId(),
                participant != null ? participant.getId() : null,
                stagiaireNom,
                encadreur != null ? encadreur.getId() : null,
                encadreurNom,
                evaluation.getAppreciationGlobale(),
                evaluation.getObservationsFinales(),
                evaluation.getEstFinalisee(),
                evaluation.getDateEvaluation(),
                evaluation.getDateFinalisation()
        );
    }

    private EvaluationCritereResponse toCritereResponse(
            EvaluationCritere evaluationCritere
    ) {
        CritereEvaluation critere = evaluationCritere.getCritere();

        return new EvaluationCritereResponse(
                evaluationCritere.getId(),
                evaluationCritere.getEvaluation().getId(),
                critere.getId(),
                critere.getCode(),
                critere.getLibelle(),
                critere.getCoefficient(),
                evaluationCritere.getNote(),
                evaluationCritere.getCommentaire()
        );
    }

    private void verifierEvaluationRequest(EvaluationFinaleRequest request) {
        if (request == null
                || request.getStageParticipantId() == null
                || request.getEncadreurId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Les informations de l'évaluation sont incomplètes"
            );
        }
    }

    private void verifierNonFinalisee(EvaluationFinale evaluation) {
        if (Boolean.TRUE.equals(evaluation.getEstFinalisee())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Une évaluation finalisée ne peut plus être modifiée"
            );
        }
    }

    private void verifierNote(BigDecimal note) {
        if (note == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La note est obligatoire"
            );
        }

        if (note.compareTo(BigDecimal.ZERO) < 0
                || note.compareTo(new BigDecimal("5")) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La note doit être comprise entre 0 et 5"
            );
        }
    }

    private String nettoyerTexteOptionnel(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            return null;
        }

        return valeur.trim();
    }
}
