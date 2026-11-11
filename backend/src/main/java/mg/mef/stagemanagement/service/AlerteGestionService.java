package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.alerte.AlerteRequest;
import mg.mef.stagemanagement.dto.alerte.AlerteResponse;
import mg.mef.stagemanagement.model.Alerte;
import mg.mef.stagemanagement.model.Encadreur;
import mg.mef.stagemanagement.model.RapportActivite;
import mg.mef.stagemanagement.model.Soutenance;
import mg.mef.stagemanagement.model.StageParticipant;
import mg.mef.stagemanagement.model.TypeAlerte;
import mg.mef.stagemanagement.model.Utilisateur;
import mg.mef.stagemanagement.model.enums.AlertePriorite;
import mg.mef.stagemanagement.model.enums.AlerteStatut;
import mg.mef.stagemanagement.repository.AlerteRepository;
import mg.mef.stagemanagement.repository.EncadreurRepository;
import mg.mef.stagemanagement.repository.RapportActiviteRepository;
import mg.mef.stagemanagement.repository.SoutenanceRepository;
import mg.mef.stagemanagement.repository.StageParticipantRepository;
import mg.mef.stagemanagement.repository.TypeAlerteRepository;
import mg.mef.stagemanagement.repository.UtilisateurRepository;
import mg.mef.stagemanagement.model.enums.NatureAlerte;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AlerteGestionService {
    private final DossierAccessService access;

    private final AlerteRepository alerteRepository;
    private final TypeAlerteRepository typeAlerteRepository;
    private final StageParticipantRepository stageParticipantRepository;
    private final RapportActiviteRepository rapportActiviteRepository;
    private final EncadreurRepository encadreurRepository;
    private final SoutenanceRepository soutenanceRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JournalAuditService journalAuditService;

    public AlerteGestionService(
            AlerteRepository alerteRepository,
            TypeAlerteRepository typeAlerteRepository,
            StageParticipantRepository stageParticipantRepository,
            RapportActiviteRepository rapportActiviteRepository,
            EncadreurRepository encadreurRepository,
            SoutenanceRepository soutenanceRepository,
            UtilisateurRepository utilisateurRepository,
            JournalAuditService journalAuditService
    , DossierAccessService access) {
        this.access = access;
        this.alerteRepository = alerteRepository;
        this.typeAlerteRepository = typeAlerteRepository;
        this.stageParticipantRepository = stageParticipantRepository;
        this.rapportActiviteRepository = rapportActiviteRepository;
        this.encadreurRepository = encadreurRepository;
        this.soutenanceRepository = soutenanceRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.journalAuditService = journalAuditService;
    }

    public List<AlerteResponse> listerAlertes() {
        return alerteRepository.findAllByOrderByDateDetectionDesc()
                .stream().filter(access::readAlert)
                .map(this::toResponse)
                .toList();
    }

    public List<AlerteResponse> listerAlertesATraiter() {
        return alerteRepository
                .findByStatutOrderByDateDetectionDesc(AlerteStatut.A_TRAITER)
                .stream().filter(access::readAlert)
                .filter(AlerteClotureService::encorePertinente)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = false)
    public AlerteResponse creerAlerte(AlerteRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Les informations de l'alerte sont obligatoires"
            );
        }

        TypeAlerte typeAlerte = trouverTypeAlerte(request.getTypeAlerteCode());

        String message = nettoyerTexteObligatoire(
                request.getMessage(),
                "Le message de l'alerte est obligatoire"
        );

        AlertePriorite priorite = convertirPriorite(request.getPriorite());
        if (request.getCleDeduplication() != null && request.getCleDeduplication().trim().length() > 255)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clé de déduplication est limitée à 255 caractères");

        if (request.getCleDeduplication() != null
                && !request.getCleDeduplication().trim().isEmpty()
                && alerteRepository.existsByCleDeduplication(
                        request.getCleDeduplication().trim()
                )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette alerte existe déjà"
            );
        }

        Alerte alerte = new Alerte();

        alerte.setTypeAlerte(typeAlerte);
        alerte.setMessage(message);
        alerte.setDateEcheance(request.getDateEcheance());
        alerte.setPriorite(priorite);
        alerte.setStatut(AlerteStatut.A_TRAITER);
        alerte.setDateDetection(LocalDateTime.now());
        alerte.setCleDeduplication(nettoyerTexteOptionnel(request.getCleDeduplication()));

        if (request.getStageParticipantId() != null) {
            StageParticipant participant = stageParticipantRepository
                    .findById(request.getStageParticipantId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Participation introuvable"
                            )
                    );

            alerte.setStageParticipant(participant);
        }

        if (request.getRapportId() != null) {
            RapportActivite rapport = rapportActiviteRepository
                    .findById(request.getRapportId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Rapport introuvable"
                            )
                    );

            alerte.setRapport(rapport);
        }

        if (request.getEncadreurId() != null) {
            Encadreur encadreur = encadreurRepository
                    .findById(request.getEncadreurId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Encadreur introuvable"
                            )
                    );

            alerte.setEncadreur(encadreur);
        }

        if (request.getSoutenanceId() != null) {
            Soutenance soutenance = soutenanceRepository
                    .findById(request.getSoutenanceId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Soutenance introuvable"
                            )
                    );

            alerte.setSoutenance(soutenance);
        }

        verifierReferences(alerte);
        access.require(access.readAlert(alerte));
        Alerte enregistree = alerteRepository.save(alerte);

        return toResponse(enregistree);
    }

    @Transactional(readOnly = false)
    public AlerteResponse marquerCommeTraitee(
            Integer alerteId,
            Integer utilisateurId
    ) {
        Alerte alerte = alerteRepository.findById(alerteId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Alerte introuvable"
                        )
                );

        access.require(access.readAlert(alerte));
        if (alerte.getTypeAlerte().getNatureAlerte()
                == NatureAlerte.INFORMATION) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Une alerte informative ne peut pas être résolue manuellement"
                );
        }

        if (alerte.getStatut() != AlerteStatut.A_TRAITER) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Cette alerte n'est plus à traiter"
                );
        }

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Utilisateur connecté introuvable"
                        )
                );

        alerte.setStatut(AlerteStatut.TRAITEE);
        alerte.setDateTraitement(LocalDateTime.now());
        alerte.setTraitePar(utilisateur);

        access.require(access.readAlert(alerte));
        Alerte enregistree = alerteRepository.save(alerte);

        journalAuditService.enregistrer(
                utilisateurId,
                "TRAITER_ALERTE",
                "Alerte",
                enregistree.getId(),
                java.util.Map.of(
                        "statut", "A_TRAITER"
                ),
                java.util.Map.of(
                        "statut", "TRAITEE"
                )
        );

        return toResponse(enregistree);
    }

        @Transactional(readOnly = false)
        public void toutMarquerCommeTraite(Integer utilisateurId) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Le traitement global des alertes est désactivé. "
                        + "Chaque anomalie doit être vérifiée individuellement."
                );
        }

    static void verifierReferences(Alerte alerte) {
        StageParticipant participant = alerte.getStageParticipant();
        if (alerte.getRapport() != null) {
            StageParticipant reportOwner = alerte.getRapport().getStageParticipant();
            if (participant != null && !participant.getId().equals(reportOwner.getId()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Le rapport ne correspond pas à la participation sélectionnée");
            participant = reportOwner;
        }
        if (alerte.getSoutenance() != null) {
            StageParticipant defenseOwner = alerte.getSoutenance().getStageParticipant();
            if (participant != null && !participant.getId().equals(defenseOwner.getId()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "La soutenance ne correspond pas au dossier sélectionné");
            participant = defenseOwner;
        }
        if (participant == null && alerte.getEncadreur() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Associez l'alerte à une participation, un rapport, un encadreur ou une soutenance");
        if (participant != null && alerte.getEncadreur() != null && !participant.getEncadreur().getId().equals(alerte.getEncadreur().getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "L'encadreur ne correspond pas au dossier sélectionné");
    }

    private TypeAlerte trouverTypeAlerte(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le type d'alerte est obligatoire"
            );
        }

        return typeAlerteRepository
                .findByCodeIgnoreCaseAndActifTrue(code.trim())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Type d'alerte introuvable"
                        )
                );
    }

    private AlertePriorite convertirPriorite(String priorite) {
        if (priorite == null || priorite.trim().isEmpty()) {
            return AlertePriorite.INFORMATION;
        }

        try {
            return AlertePriorite.valueOf(priorite.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Priorité d'alerte invalide"
            );
        }
    }

    private AlerteResponse toResponse(Alerte alerte) {
        TypeAlerte typeAlerte = alerte.getTypeAlerte();
        Utilisateur traitePar = alerte.getTraitePar();

        AlerteResponse response = new AlerteResponse(
                alerte.getId(),
                typeAlerte != null ? typeAlerte.getCode() : null,
                typeAlerte != null ? typeAlerte.getLibelle() : null,
                typeAlerte != null ? typeAlerte.getActionRecommandee() : null,
                alerte.getStageParticipant() != null
                        ? alerte.getStageParticipant().getId()
                        : null,
                alerte.getRapport() != null
                        ? alerte.getRapport().getId()
                        : null,
                alerte.getEncadreur() != null
                        ? alerte.getEncadreur().getId()
                        : null,
                alerte.getSoutenance() != null
                        ? alerte.getSoutenance().getId()
                        : null,
                alerte.getMessage(),
                alerte.getDateEcheance(),
                alerte.getPriorite() != null ? alerte.getPriorite().name() : null,
                alerte.getStatut() != null ? alerte.getStatut().name() : null,
                alerte.getDateDetection(),
                alerte.getDateTraitement(),
                traitePar != null ? traitePar.getId() : null,
                traitePar != null ? traitePar.getIdentifiant() : null
        );

        response.setNatureAlerte(
                typeAlerte != null && typeAlerte.getNatureAlerte() != null
                        ? typeAlerte.getNatureAlerte().name()
                        : null
        );

        return response;
    }

    private String nettoyerTexteObligatoire(String valeur, String message) {
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
