package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.rapport.ChangerStatutRapportRequest;
import mg.mef.stagemanagement.dto.rapport.RapportActiviteRequest;
import mg.mef.stagemanagement.dto.rapport.RapportActiviteResponse;
import mg.mef.stagemanagement.dto.rapport.RapportObservationRequest;
import mg.mef.stagemanagement.dto.rapport.RapportObservationResponse;
import mg.mef.stagemanagement.model.Encadreur;
import mg.mef.stagemanagement.model.MoisStage;
import mg.mef.stagemanagement.model.RapportActivite;
import mg.mef.stagemanagement.model.RapportActiviteStatutHistorique;
import mg.mef.stagemanagement.model.RapportObservation;
import mg.mef.stagemanagement.model.StageParticipant;
import mg.mef.stagemanagement.model.Stagiaire;
import mg.mef.stagemanagement.model.StagiaireAcademique;
import mg.mef.stagemanagement.model.Statut;
import mg.mef.stagemanagement.model.Utilisateur;
import mg.mef.stagemanagement.model.enums.TypeObservationRapport;
import mg.mef.stagemanagement.repository.MoisStageRepository;
import mg.mef.stagemanagement.repository.RapportActiviteRepository;
import mg.mef.stagemanagement.repository.RapportActiviteStatutHistoriqueRepository;
import mg.mef.stagemanagement.repository.RapportObservationRepository;
import mg.mef.stagemanagement.repository.StageParticipantRepository;
import mg.mef.stagemanagement.repository.StatutRepository;
import mg.mef.stagemanagement.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RapportActiviteGestionService {

    private final RapportActiviteRepository rapportActiviteRepository;
    private final RapportObservationRepository rapportObservationRepository;
    private final RapportActiviteStatutHistoriqueRepository historiqueRepository;
    private final StageParticipantRepository stageParticipantRepository;
    private final MoisStageRepository moisStageRepository;
    private final StatutRepository statutRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JournalAuditService journalAuditService;

    public RapportActiviteGestionService(
            RapportActiviteRepository rapportActiviteRepository,
            RapportObservationRepository rapportObservationRepository,
            RapportActiviteStatutHistoriqueRepository historiqueRepository,
            StageParticipantRepository stageParticipantRepository,
            MoisStageRepository moisStageRepository,
            StatutRepository statutRepository,
            UtilisateurRepository utilisateurRepository,
            JournalAuditService journalAuditService
    ) {
        this.rapportActiviteRepository = rapportActiviteRepository;
        this.rapportObservationRepository = rapportObservationRepository;
        this.historiqueRepository = historiqueRepository;
        this.stageParticipantRepository = stageParticipantRepository;
        this.moisStageRepository = moisStageRepository;
        this.statutRepository = statutRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.journalAuditService = journalAuditService;
    }

    public List<RapportActiviteResponse> listerRapports() {
        return rapportActiviteRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(RapportActivite::getDebutSemaine).reversed())
                .map(this::toRapportResponse)
                .toList();
    }

    public RapportActiviteResponse trouverRapport(Integer id) {
        RapportActivite rapport = rapportActiviteRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Rapport introuvable"
                        )
                );

        return toRapportResponse(rapport);
    }

    public List<RapportActiviteResponse> listerRapportsParParticipation(
            Integer stageParticipantId
    ) {
        if (!stageParticipantRepository.existsById(stageParticipantId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Participation introuvable"
            );
        }

        return rapportActiviteRepository
                .findByStageParticipant_Id(stageParticipantId)
                .stream()
                .sorted(Comparator.comparing(RapportActivite::getDebutSemaine).reversed())
                .map(this::toRapportResponse)
                .toList();
    }

    @Transactional(readOnly = false)
    public RapportActiviteResponse creerRapport(
            RapportActiviteRequest request
    ) {
        verifierRapportRequest(request);

        if (request.getFinSemaine().isBefore(request.getDebutSemaine())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fin de semaine doit être après le début de semaine"
            );
        }

        if (rapportActiviteRepository.existsByStageParticipant_IdAndDebutSemaineAndFinSemaine(
                request.getStageParticipantId(),
                request.getDebutSemaine(),
                request.getFinSemaine()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un rapport existe déjà pour cette période"
            );
        }

        StageParticipant participant =
                stageParticipantRepository.findById(request.getStageParticipantId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Participation introuvable"
                                )
                        );

        MoisStage moisStage = moisStageRepository.findById(request.getMoisStageId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Mois de stage introuvable"
                        )
                );

        Statut statutBrouillon = statutRepository
                .findStatutByTypeCodeAndStatutCode("RAPPORT", "BROUILLON")
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Statut BROUILLON du rapport introuvable"
                        )
                );

        RapportActivite rapport = new RapportActivite();

        rapport.setStageParticipant(participant);
        rapport.setDebutSemaine(request.getDebutSemaine());
        rapport.setFinSemaine(request.getFinSemaine());
        rapport.setActivite(nettoyerTexteOptionnel(request.getActivite()));
        rapport.setDifficultes(nettoyerTexteOptionnel(request.getDifficultes()));
        rapport.setSolutions(nettoyerTexteOptionnel(request.getSolutions()));
        rapport.setMoisStage(moisStage);
        rapport.setStatut(statutBrouillon);
        rapport.setDateCreation(LocalDateTime.now());
        rapport.setDateModification(LocalDateTime.now());

        RapportActivite enregistre =
                rapportActiviteRepository.save(rapport);

        return toRapportResponse(enregistre);
    }

    @Transactional(readOnly = false)
    public RapportActiviteResponse modifierRapport(
            Integer id,
            RapportActiviteRequest request
    ) {
        verifierRapportRequest(request);

        if (request.getFinSemaine().isBefore(request.getDebutSemaine())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fin de semaine doit être après le début de semaine"
            );
        }

        RapportActivite rapport = rapportActiviteRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Rapport introuvable"
                        )
                );

        String statutActuel = rapport.getStatut() != null
                ? rapport.getStatut().getCode()
                : null;

        if ("VALIDE".equals(statutActuel)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un rapport validé ne peut plus être modifié"
            );
        }

        StageParticipant participant =
                stageParticipantRepository.findById(request.getStageParticipantId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Participation introuvable"
                                )
                        );

        MoisStage moisStage = moisStageRepository.findById(request.getMoisStageId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Mois de stage introuvable"
                        )
                );

        rapport.setStageParticipant(participant);
        rapport.setDebutSemaine(request.getDebutSemaine());
        rapport.setFinSemaine(request.getFinSemaine());
        rapport.setActivite(nettoyerTexteOptionnel(request.getActivite()));
        rapport.setDifficultes(nettoyerTexteOptionnel(request.getDifficultes()));
        rapport.setSolutions(nettoyerTexteOptionnel(request.getSolutions()));
        rapport.setMoisStage(moisStage);
        rapport.setDateModification(LocalDateTime.now());

        RapportActivite enregistre =
                rapportActiviteRepository.save(rapport);

        return toRapportResponse(enregistre);
    }

    public List<RapportObservationResponse> listerObservations(
            Integer rapportId
    ) {
        if (!rapportActiviteRepository.existsById(rapportId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Rapport introuvable"
            );
        }

        return rapportObservationRepository
                .findByRapport_IdOrderByDateCreationAsc(rapportId)
                .stream()
                .map(this::toObservationResponse)
                .toList();
    }

    @Transactional(readOnly = false)
    public RapportObservationResponse ajouterObservation(
            Integer rapportId,
            RapportObservationRequest request,
            Integer auteurId
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Les informations de l'observation sont obligatoires"
            );
        }

        RapportActivite rapport = rapportActiviteRepository.findById(rapportId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Rapport introuvable"
                        )
                );

        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Auteur introuvable"
                        )
                );

        TypeObservationRapport typeObservation =
                convertirTypeObservation(request.getTypeObservation());

        String commentaire = nettoyerTexteObligatoire(
                request.getCommentaire(),
                "Le commentaire est obligatoire"
        );

        RapportObservation observation = new RapportObservation();

        observation.setRapport(rapport);
        observation.setAuteur(auteur);
        observation.setTypeObservation(typeObservation);
        observation.setCommentaire(commentaire);
        observation.setVisibleStagiaire(
                request.getVisibleStagiaire() == null
                        || request.getVisibleStagiaire()
        );
        observation.setDateCreation(LocalDateTime.now());

        RapportObservation enregistree =
                rapportObservationRepository.save(observation);

        return toObservationResponse(enregistree);
    }

    @Transactional(readOnly = false)
    public RapportActiviteResponse changerStatutRapport(
            Integer rapportId,
            ChangerStatutRapportRequest request,
            Integer responsableId
    ) {
        if (request == null
                || request.getStatutCode() == null
                || request.getStatutCode().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le statut est obligatoire"
            );
        }

        RapportActivite rapport = rapportActiviteRepository.findById(rapportId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Rapport introuvable"
                        )
                );

        Utilisateur responsable = utilisateurRepository.findById(responsableId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Responsable introuvable"
                        )
                );

        String statutCode = request.getStatutCode().trim().toUpperCase();

        Statut nouveauStatut = statutRepository
                .findStatutByTypeCodeAndStatutCode("RAPPORT", statutCode)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Statut de rapport invalide"
                        )
                );

        Statut ancienStatut = rapport.getStatut();

        if (ancienStatut != null
                && ancienStatut.getId().equals(nouveauStatut.getId())) {
            return toRapportResponse(rapport);
        }

        rapport.setStatut(nouveauStatut);
        rapport.setDateModification(LocalDateTime.now());

        if ("SOUMIS".equals(statutCode)) {
            rapport.setDateSoumission(LocalDateTime.now());
        }

        if ("VALIDE".equals(statutCode)) {
            rapport.setDateValidation(LocalDateTime.now());
        }

        RapportActivite rapportEnregistre =
                rapportActiviteRepository.save(rapport);

        RapportActiviteStatutHistorique historique =
                new RapportActiviteStatutHistorique();

        historique.setRapport(rapportEnregistre);
        historique.setAncienStatut(ancienStatut);
        historique.setStatut(nouveauStatut);
        historique.setResponsable(responsable);
        historique.setMotif(request.getMotif());
        historique.setDateModification(LocalDateTime.now());

        historiqueRepository.save(historique);

       java.util.Map<String, Object> anciennesValeurs = new java.util.HashMap<>();
        anciennesValeurs.put(
                "statut",
                ancienStatut != null ? ancienStatut.getCode() : null
        );

        java.util.Map<String, Object> nouvellesValeurs = new java.util.HashMap<>();
        nouvellesValeurs.put("statut", nouveauStatut.getCode());

        journalAuditService.enregistrer(
                responsableId,
                "CHANGER_STATUT_RAPPORT",
                "RapportActivite",
                rapportEnregistre.getId(),
                anciennesValeurs,
                nouvellesValeurs
        );

        return toRapportResponse(rapportEnregistre);
    }

    private RapportActiviteResponse toRapportResponse(
            RapportActivite rapport
    ) {
        StageParticipant participant = rapport.getStageParticipant();

        String stagiaireNom = null;
        String encadreurNom = null;

        if (participant != null && participant.getStagiaireAcademique() != null) {
            StagiaireAcademique academique =
                    participant.getStagiaireAcademique();

            Stagiaire stagiaire = academique.getStagiaire();

            if (stagiaire != null) {
                stagiaireNom = stagiaire.getNom() + " " + stagiaire.getPrenoms();
            }
        }

        if (participant != null && participant.getEncadreur() != null) {
            Encadreur encadreur = participant.getEncadreur();
            encadreurNom = encadreur.getNom() + " " + encadreur.getPrenoms();
        }

        String statut = rapport.getStatut() != null
                ? rapport.getStatut().getCode()
                : null;

        return new RapportActiviteResponse(
                rapport.getId(),
                participant != null ? participant.getId() : null,
                stagiaireNom,
                encadreurNom,
                rapport.getDebutSemaine(),
                rapport.getFinSemaine(),
                rapport.getActivite(),
                rapport.getDifficultes(),
                rapport.getSolutions(),
                rapport.getMoisStage() != null ? rapport.getMoisStage().getId() : null,
                rapport.getMoisStage() != null ? rapport.getMoisStage().getLibelle() : null,
                statut,
                rapport.getDateCreation(),
                rapport.getDateModification(),
                rapport.getDateSoumission(),
                rapport.getDateValidation()
        );
    }

    private RapportObservationResponse toObservationResponse(
            RapportObservation observation
    ) {
        Utilisateur auteur = observation.getAuteur();

        return new RapportObservationResponse(
                observation.getId(),
                observation.getRapport().getId(),
                auteur != null ? auteur.getId() : null,
                auteur != null ? auteur.getIdentifiant() : null,
                observation.getTypeObservation() != null
                        ? observation.getTypeObservation().name()
                        : null,
                observation.getCommentaire(),
                observation.getVisibleStagiaire(),
                observation.getDateCreation()
        );
    }

    private void verifierRapportRequest(RapportActiviteRequest request) {
        if (request == null
                || request.getStageParticipantId() == null
                || request.getDebutSemaine() == null
                || request.getFinSemaine() == null
                || request.getMoisStageId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Les informations du rapport sont incomplètes"
            );
        }
    }

    private TypeObservationRapport convertirTypeObservation(String type) {
        if (type == null || type.trim().isEmpty()) {
            return TypeObservationRapport.OBSERVATION;
        }

        try {
            return TypeObservationRapport.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Type d'observation invalide"
            );
        }
    }

    private String nettoyerTexteObligatoire(
            String valeur,
            String message
    ) {
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    message
            );
        }

        return valeur.trim();
    }

    private String nettoyerTexteOptionnel(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            return null;
        }

        return valeur.trim();
    }
}