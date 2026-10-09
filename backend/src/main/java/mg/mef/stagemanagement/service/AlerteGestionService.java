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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AlerteGestionService {

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
    ) {
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
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AlerteResponse> listerAlertesATraiter() {
        return alerteRepository
                .findByStatutOrderByDateDetectionDesc(AlerteStatut.A_TRAITER)
                .stream()
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
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Utilisateur connecté introuvable"
                        )
                );

        List<Alerte> alertes =
                alerteRepository.findByStatutOrderByDateDetectionDesc(
                        AlerteStatut.A_TRAITER
                );

        for (Alerte alerte : alertes) {
            alerte.setStatut(AlerteStatut.TRAITEE);
            alerte.setDateTraitement(LocalDateTime.now());
            alerte.setTraitePar(utilisateur);
        }

        alerteRepository.saveAll(alertes);
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

        return new AlerteResponse(
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