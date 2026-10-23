package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.stage.StageParticipantRequest;
import mg.mef.stagemanagement.dto.stage.StageParticipantResponse;
import mg.mef.stagemanagement.dto.stage.StageRequest;
import mg.mef.stagemanagement.dto.stage.StageResponse;
import mg.mef.stagemanagement.model.CadreStage;
import mg.mef.stagemanagement.model.Encadreur;
import mg.mef.stagemanagement.model.Stage;
import mg.mef.stagemanagement.model.StageParticipant;
import mg.mef.stagemanagement.model.Stagiaire;
import mg.mef.stagemanagement.model.StagiaireAcademique;
import mg.mef.stagemanagement.model.Statut;
import mg.mef.stagemanagement.model.StructureAccueil;
import mg.mef.stagemanagement.repository.CadreStageRepository;
import mg.mef.stagemanagement.repository.EncadreurRepository;
import mg.mef.stagemanagement.repository.StageParticipantRepository;
import mg.mef.stagemanagement.repository.StageRepository;
import mg.mef.stagemanagement.repository.StagiaireAcademiqueRepository;
import mg.mef.stagemanagement.repository.StatutRepository;
import mg.mef.stagemanagement.repository.StructureAccueilRepository;
import mg.mef.stagemanagement.dto.stage.ChangerStatutParticipationRequest;
import mg.mef.stagemanagement.dto.stage.ProlongerParticipationRequest;
import mg.mef.stagemanagement.model.StageParticipantEvenement;
import mg.mef.stagemanagement.model.StageParticipantStatutHistorique;
import mg.mef.stagemanagement.model.TypeEvenementParticipation;
import mg.mef.stagemanagement.model.Utilisateur;
import mg.mef.stagemanagement.repository.StageParticipantEvenementRepository;
import mg.mef.stagemanagement.repository.StageParticipantStatutHistoriqueRepository;
import mg.mef.stagemanagement.repository.TypeEvenementParticipationRepository;
import mg.mef.stagemanagement.repository.UtilisateurRepository;
import mg.mef.stagemanagement.dto.stage.EvenementParticipationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class StageGestionService {

    private final StageRepository stageRepository;
    private final StageParticipantRepository stageParticipantRepository;
    private final StructureAccueilRepository structureAccueilRepository;
    private final StagiaireAcademiqueRepository stagiaireAcademiqueRepository;
    private final CadreStageRepository cadreStageRepository;
    private final EncadreurRepository encadreurRepository;
    private final StatutRepository statutRepository;
    private final StageParticipantStatutHistoriqueRepository stageParticipantStatutHistoriqueRepository;
    private final StageParticipantEvenementRepository stageParticipantEvenementRepository;
    private final TypeEvenementParticipationRepository typeEvenementParticipationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public StageGestionService(
            StageRepository stageRepository,
            StageParticipantRepository stageParticipantRepository,
            StructureAccueilRepository structureAccueilRepository,
            StagiaireAcademiqueRepository stagiaireAcademiqueRepository,
            CadreStageRepository cadreStageRepository,
            EncadreurRepository encadreurRepository,
            StatutRepository statutRepository,
            StageParticipantStatutHistoriqueRepository stageParticipantStatutHistoriqueRepository,
            StageParticipantEvenementRepository stageParticipantEvenementRepository,
            TypeEvenementParticipationRepository typeEvenementParticipationRepository,
            UtilisateurRepository utilisateurRepository
    ) {
        this.stageRepository = stageRepository;
        this.stageParticipantRepository = stageParticipantRepository;
        this.structureAccueilRepository = structureAccueilRepository;
        this.stagiaireAcademiqueRepository = stagiaireAcademiqueRepository;
        this.cadreStageRepository = cadreStageRepository;
        this.encadreurRepository = encadreurRepository;
        this.statutRepository = statutRepository;
        this.stageParticipantStatutHistoriqueRepository = stageParticipantStatutHistoriqueRepository;
        this.stageParticipantEvenementRepository = stageParticipantEvenementRepository;
        this.typeEvenementParticipationRepository = typeEvenementParticipationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // =========================
    // STAGES
    // =========================

    public List<StageResponse> listerStages() {
        return stageRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Stage::getLibelle))
                .map(this::toStageResponse)
                .toList();
    }

    public StageResponse trouverStage(Integer id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Stage introuvable"
                        )
                );

        return toStageResponse(stage);
    }

    @Transactional(readOnly = false)
    public StageResponse creerStage(StageRequest request) {
        if (request == null || request.getStructureAccueilId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La structure d'accueil est obligatoire"
            );
        }

        String libelle = nettoyerTexteObligatoire(
                request.getLibelle(),
                "Le libellé du stage est obligatoire",
                150
        );

        StructureAccueil structureAccueil =
                structureAccueilRepository.findById(request.getStructureAccueilId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Structure d'accueil introuvable"
                                )
                        );

        if (stageRepository.existsByLibelleIgnoreCaseAndStructureAccueil_Id(
                libelle,
                structureAccueil.getId()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce stage existe déjà dans cette structure d'accueil"
            );
        }

        Stage stage = new Stage();
        stage.setLibelle(libelle);
        stage.setStructureAccueil(structureAccueil);
        stage.setDateCreation(LocalDateTime.now());

        Stage enregistre = stageRepository.save(stage);

        return toStageResponse(enregistre);
    }

    @Transactional(readOnly = false)
    public StageResponse modifierStage(
            Integer id,
            StageRequest request
    ) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Stage introuvable"
                        )
                );

        if (request == null || request.getStructureAccueilId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La structure d'accueil est obligatoire"
            );
        }

        String libelle = nettoyerTexteObligatoire(
                request.getLibelle(),
                "Le libellé du stage est obligatoire",
                150
        );

        StructureAccueil structureAccueil =
                structureAccueilRepository.findById(request.getStructureAccueilId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Structure d'accueil introuvable"
                                )
                        );

        stageRepository
                .findByLibelleIgnoreCaseAndStructureAccueil_Id(
                        libelle,
                        structureAccueil.getId()
                )
                .ifPresent(existant -> {
                    if (!existant.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ce stage existe déjà dans cette structure d'accueil"
                        );
                    }
                });

        stage.setLibelle(libelle);
        stage.setStructureAccueil(structureAccueil);

        Stage enregistre = stageRepository.save(stage);

        return toStageResponse(enregistre);
    }

    // =========================
    // PARTICIPATIONS
    // =========================

    public List<StageParticipantResponse> listerParticipations() {
        return stageParticipantRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(StageParticipant::getDateDebutStage))
                .map(this::toParticipantResponse)
                .toList();
    }

    public StageParticipantResponse trouverParticipation(Integer id) {
        StageParticipant participant = stageParticipantRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Participation introuvable"
                        )
                );

        return toParticipantResponse(participant);
    }

    public List<StageParticipantResponse> listerParticipationsParStage(
            Integer stageId
    ) {
        if (!stageRepository.existsById(stageId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Stage introuvable"
            );
        }

        return stageParticipantRepository.findByStage_Id(stageId)
                .stream()
                .map(this::toParticipantResponse)
                .toList();
    }

    @Transactional(readOnly = false)
    public StageParticipantResponse creerParticipation(
            StageParticipantRequest request
    ) {
        verifierParticipationRequest(request);

        if (request.getDateFinStage().isBefore(request.getDateDebutStage())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La date de fin doit être après la date de début"
            );
        }

        Stage stage = stageRepository.findById(request.getStageId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Stage introuvable"
                        )
                );

        StagiaireAcademique stagiaireAcademique =
                stagiaireAcademiqueRepository.findById(request.getStagiaireAcademiqueId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Profil académique introuvable"
                                )
                        );

        CadreStage cadreStage =
                cadreStageRepository.findById(request.getCadreStageId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Cadre de stage introuvable"
                                )
                        );

        Encadreur encadreur =
                encadreurRepository.findById(request.getEncadreurId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Encadreur introuvable"
                                )
                        );

        if (stageParticipantRepository.existsByStage_IdAndStagiaireAcademique_IdAndDateDebutStage(
                stage.getId(),
                stagiaireAcademique.getId(),
                request.getDateDebutStage()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette participation existe déjà pour ce stagiaire et cette date de début"
            );
        }

        Statut statutPlanifie = statutRepository
                .findStatutByTypeCodeAndStatutCode("PARTICIPATION", "PLANIFIEE")
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Statut PLANIFIEE introuvable"
                        )
                );

        StageParticipant participant = new StageParticipant();

        participant.setStage(stage);
        participant.setStagiaireAcademique(stagiaireAcademique);
        participant.setCadreStage(cadreStage);
        participant.setEncadreur(encadreur);
        participant.setDateDebutStage(request.getDateDebutStage());
        participant.setDateFinStage(request.getDateFinStage());
        participant.setStatut(statutPlanifie);
        participant.setDateCreation(LocalDateTime.now());
        participant.setDateModification(LocalDateTime.now());

        StageParticipant enregistre =
                stageParticipantRepository.save(participant);

        return toParticipantResponse(enregistre);
    }

    @Transactional(readOnly = false)
    public StageParticipantResponse modifierParticipation(
            Integer id,
            StageParticipantRequest request
    ) {
        verifierParticipationRequest(request);

        if (request.getDateFinStage().isBefore(request.getDateDebutStage())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La date de fin doit être après la date de début"
            );
        }

        StageParticipant participant = stageParticipantRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Participation introuvable"
                        )
                );

        Stage stage = stageRepository.findById(request.getStageId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Stage introuvable"
                        )
                );

        StagiaireAcademique stagiaireAcademique =
                stagiaireAcademiqueRepository.findById(request.getStagiaireAcademiqueId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Profil académique introuvable"
                                )
                        );

        CadreStage cadreStage =
                cadreStageRepository.findById(request.getCadreStageId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Cadre de stage introuvable"
                                )
                        );

        Encadreur encadreur =
                encadreurRepository.findById(request.getEncadreurId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Encadreur introuvable"
                                )
                        );

        participant.setStage(stage);
        participant.setStagiaireAcademique(stagiaireAcademique);
        participant.setCadreStage(cadreStage);
        participant.setEncadreur(encadreur);
        participant.setDateDebutStage(request.getDateDebutStage());
        participant.setDateFinStage(request.getDateFinStage());
        participant.setDateModification(LocalDateTime.now());

        StageParticipant enregistre =
                stageParticipantRepository.save(participant);

        return toParticipantResponse(enregistre);
    }

    // =========================
    // MAPPERS
    // =========================

    private StageResponse toStageResponse(Stage stage) {
        StructureAccueil structureAccueil = stage.getStructureAccueil();

        Integer departementId = null;
        String departement = null;

        if (structureAccueil != null && structureAccueil.getDepartement() != null) {
            departementId = structureAccueil.getDepartement().getId();
            departement = structureAccueil.getDepartement().getLibelle();
        }

        return new StageResponse(
                stage.getId(),
                stage.getLibelle(),
                structureAccueil != null ? structureAccueil.getId() : null,
                structureAccueil != null ? structureAccueil.getLibelle() : null,
                departementId,
                departement,
                stage.getDateCreation()
        );
    }

    private StageParticipantResponse toParticipantResponse(
            StageParticipant participant
    ) {
        Stage stage = participant.getStage();
        StagiaireAcademique academique = participant.getStagiaireAcademique();
        Stagiaire stagiaire = academique.getStagiaire();
        CadreStage cadreStage = participant.getCadreStage();
        Encadreur encadreur = participant.getEncadreur();

        String stagiaireNomComplet =
                stagiaire.getNom() + " " + stagiaire.getPrenoms();

        String encadreurNomComplet =
                encadreur.getNom() + " " + encadreur.getPrenoms();

        String statut = participant.getStatut() != null
                ? participant.getStatut().getCode()
                : null;

        return new StageParticipantResponse(
                participant.getId(),
                stage.getId(),
                stage.getLibelle(),
                academique.getId(),
                stagiaire.getId(),
                stagiaireNomComplet,
                academique.getNiveauEtude().getLibelle(),
                academique.getFiliere().getLibelle(),
                academique.getEtablissement().getLibelle(),
                academique.getAnneeEtudes(),
                cadreStage.getId(),
                cadreStage.getThemeStage(),
                encadreur.getId(),
                encadreurNomComplet,
                participant.getDateDebutStage(),
                participant.getDateFinStage(),
                statut,
                participant.getDateCreation(),
                participant.getDateModification(),
                participant.getDateCloture()
        );
    }

    // =========================
    // UTILITAIRES
    // =========================

    private void verifierParticipationRequest(StageParticipantRequest request) {
        if (request == null
                || request.getStageId() == null
                || request.getStagiaireAcademiqueId() == null
                || request.getCadreStageId() == null
                || request.getEncadreurId() == null
                || request.getDateDebutStage() == null
                || request.getDateFinStage() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Les informations de participation sont incomplètes"
            );
        }
    }

    private String nettoyerTexteObligatoire(
            String valeur,
            String message,
            int tailleMax
    ) {
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    message
            );
        }

        String texte = valeur.trim();

        if (texte.length() > tailleMax) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le texte est trop long"
            );
        }

        return texte;
    }

    @Transactional(readOnly = false)
    public StageParticipantResponse changerStatutParticipation(
            Integer participationId,
            ChangerStatutParticipationRequest request,
            Integer responsableId
    ) {

        if (request == null
                || request.getStatutCode() == null
                || request.getStatutCode().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le nouveau statut est obligatoire"
            );
        }

        StageParticipant participant = stageParticipantRepository.findById(participationId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Participation introuvable"
                        )
                );

        Utilisateur responsable = utilisateurRepository.findById(responsableId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Responsable introuvable"
                        )
                );

        String nouveauStatutCode =
                request.getStatutCode().trim().toUpperCase();

        Statut nouveauStatut = statutRepository
                .findStatutByTypeCodeAndStatutCode(
                        "PARTICIPATION",
                        nouveauStatutCode
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Statut de participation invalide"
                        )
                );

        Statut ancienStatut = participant.getStatut();

        if (ancienStatut != null
                && ancienStatut.getId().equals(nouveauStatut.getId())) {

            return toParticipantResponse(participant);
        }

        participant.setStatut(nouveauStatut);
        participant.setDateModification(LocalDateTime.now());

        StageParticipant participantEnregistre =
                stageParticipantRepository.save(participant);

        StageParticipantStatutHistorique historique =
                new StageParticipantStatutHistorique();

        historique.setStageParticipant(participantEnregistre);
        historique.setAncienStatut(ancienStatut);
        historique.setStatut(nouveauStatut);
        historique.setResponsable(responsable);
        historique.setMotif(request.getMotif());
        historique.setDateModification(LocalDateTime.now());

        stageParticipantStatutHistoriqueRepository.save(historique);

        return toParticipantResponse(participantEnregistre);
    }

    @Transactional(readOnly = false)
    public StageParticipantResponse prolongerParticipation(
            Integer participationId,
            ProlongerParticipationRequest request,
            Integer responsableId
    ) {

        if (request == null || request.getNouvelleDateFin() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nouvelle date de fin est obligatoire"
            );
        }

        StageParticipant participant = stageParticipantRepository.findById(participationId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Participation introuvable"
                        )
                );

        Utilisateur responsable = utilisateurRepository.findById(responsableId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Responsable introuvable"
                        )
                );

        if (!request.getNouvelleDateFin().isAfter(participant.getDateFinStage())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nouvelle date de fin doit être après l'ancienne date de fin"
            );
        }

        TypeEvenementParticipation typeEvenement =
                typeEvenementParticipationRepository.findByCode("PROLONGATION")
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Type d'événement PROLONGATION introuvable"
                                )
                        );

        StageParticipantEvenement evenement =
                new StageParticipantEvenement();

        evenement.setStageParticipant(participant);
        evenement.setTypeEvenement(typeEvenement);
        evenement.setDateEvenement(LocalDateTime.now());
        evenement.setAncienneDateFin(participant.getDateFinStage());
        evenement.setNouvelleDateFin(request.getNouvelleDateFin());
        evenement.setMotif(request.getMotif());
        evenement.setResponsable(responsable);

        stageParticipantEvenementRepository.save(evenement);

        participant.setDateFinStage(request.getNouvelleDateFin());
        participant.setDateModification(LocalDateTime.now());

        StageParticipant participantEnregistre =
                stageParticipantRepository.save(participant);

        return toParticipantResponse(participantEnregistre);
    }

    @Transactional(readOnly = false)
    public StageParticipantResponse suspendreParticipation(
            Integer participationId,
            EvenementParticipationRequest request,
            Integer responsableId
    ) {
        return appliquerEvenementAvecStatut(
                participationId,
                "SUSPENSION",
                "SUSPENDUE",
                request,
                responsableId
        );
    }

    @Transactional(readOnly = false)
    public StageParticipantResponse reprendreParticipation(
            Integer participationId,
            EvenementParticipationRequest request,
            Integer responsableId
    ) {
        return appliquerEvenementAvecStatut(
                participationId,
                "REPRISE",
                "EN_COURS",
                request,
                responsableId
        );
    }

    @Transactional(readOnly = false)
    public StageParticipantResponse abandonnerParticipation(
            Integer participationId,
            EvenementParticipationRequest request,
            Integer responsableId
    ) {
        return appliquerEvenementAvecStatut(
                participationId,
                "ABANDON",
                "ABANDONNEE",
                request,
                responsableId
        );
    }

    @Transactional(readOnly = false)
    public StageParticipantResponse annulerParticipation(
            Integer participationId,
            EvenementParticipationRequest request,
            Integer responsableId
    ) {
        return appliquerEvenementAvecStatut(
                participationId,
                "ANNULATION",
                "ANNULEE",
                request,
                responsableId
        );
    }

    @Transactional(readOnly = false)
    public StageParticipantResponse cloturerParticipation(
            Integer participationId,
            EvenementParticipationRequest request,
            Integer responsableId
    ) {
        StageParticipant participant = stageParticipantRepository.findById(participationId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Participation introuvable"
                        )
                );

        if (participant.getDateCloture() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette participation est déjà clôturée"
            );
        }

        StageParticipantResponse response = appliquerEvenementAvecStatut(
                participationId,
                "CLOTUREE",
                "TERMINEE",
                request,
                responsableId
        );

        participant = stageParticipantRepository.findById(participationId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Participation introuvable"
                        )
                );

        participant.setDateCloture(LocalDateTime.now());
        participant.setDateModification(LocalDateTime.now());

        StageParticipant enregistre = stageParticipantRepository.save(participant);

        return toParticipantResponse(enregistre);
    }

    private StageParticipantResponse appliquerEvenementAvecStatut(
            Integer participationId,
            String typeEvenementCode,
            String nouveauStatutCode,
            EvenementParticipationRequest request,
            Integer responsableId
    ) {
        StageParticipant participant = stageParticipantRepository.findById(participationId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Participation introuvable"
                        )
                );

        Utilisateur responsable = utilisateurRepository.findById(responsableId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Responsable introuvable"
                        )
                );

        TypeEvenementParticipation typeEvenement =
                typeEvenementParticipationRepository.findByCode(typeEvenementCode)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Type d'événement introuvable : " + typeEvenementCode
                                )
                        );

        Statut nouveauStatut = statutRepository
                .findStatutByTypeCodeAndStatutCode(
                        "PARTICIPATION",
                        nouveauStatutCode
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Statut de participation introuvable : " + nouveauStatutCode
                        )
                );

        Statut ancienStatut = participant.getStatut();

        
        LocalDate dateEffet = null;

        if ("SUSPENSION".equals(typeEvenementCode)
                || "REPRISE".equals(typeEvenementCode)) {

        if (request == null || request.getDateEffet() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La date d'effet est obligatoire pour une suspension ou une reprise"
                );
        }

        dateEffet = request.getDateEffet();

        if (dateEffet.isBefore(participant.getDateDebutStage())
                || dateEffet.isAfter(participant.getDateFinStage())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La date d'effet doit appartenir à la période du stage"
                );
        }

        if ("SUSPENSION".equals(typeEvenementCode)
                && (ancienStatut == null
                        || !"EN_COURS".equals(ancienStatut.getCode()))) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Seule une participation en cours peut être suspendue"
                );
        }

        if ("REPRISE".equals(typeEvenementCode)
                && (ancienStatut == null
                        || !"SUSPENDUE".equals(ancienStatut.getCode()))) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Seule une participation suspendue peut reprendre"
                );
        }

        if (dateEffet.isAfter(LocalDate.now(
                java.time.ZoneId.of("Indian/Antananarivo")))) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La date d'effet ne peut pas être dans le futur"
                );
        }

        var evenementsPrecedents = stageParticipantEvenementRepository
                .findByStageParticipant_IdOrderByDateEvenementAscIdAsc(
                        participationId
                );

        LocalDate derniereDateEffet = evenementsPrecedents.stream()
                .filter(e -> e.getTypeEvenement() != null)
                .filter(e -> "SUSPENSION".equals(e.getTypeEvenement().getCode())
                        || "REPRISE".equals(e.getTypeEvenement().getCode()))
                .map(StageParticipantEvenement::getDateEffet)
                .filter(java.util.Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);

        if (derniereDateEffet != null
                && !dateEffet.isAfter(derniereDateEffet)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "La date d'effet doit être postérieure à celle du dernier événement de suspension ou de reprise"
                        );
                }
        }


        StageParticipantEvenement evenement = new StageParticipantEvenement();

        evenement.setStageParticipant(participant);
        evenement.setTypeEvenement(typeEvenement);
        evenement.setDateEvenement(LocalDateTime.now());
        evenement.setDateEffet(dateEffet);
        evenement.setMotif(request != null ? request.getMotif() : null);
        evenement.setResponsable(responsable);

        stageParticipantEvenementRepository.save(evenement);

        participant.setStatut(nouveauStatut);
        participant.setDateModification(LocalDateTime.now());

        StageParticipant participantEnregistre =
                stageParticipantRepository.save(participant);

        StageParticipantStatutHistorique historique =
                new StageParticipantStatutHistorique();

        historique.setStageParticipant(participantEnregistre);
        historique.setAncienStatut(ancienStatut);
        historique.setStatut(nouveauStatut);
        historique.setResponsable(responsable);
        historique.setMotif(request != null ? request.getMotif() : null);
        historique.setDateModification(LocalDateTime.now());

        stageParticipantStatutHistoriqueRepository.save(historique);

        return toParticipantResponse(participantEnregistre);
    }
}