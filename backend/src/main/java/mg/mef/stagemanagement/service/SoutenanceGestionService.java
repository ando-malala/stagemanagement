package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.soutenance.SoutenanceRequest;
import mg.mef.stagemanagement.dto.soutenance.SoutenanceResponse;
import mg.mef.stagemanagement.model.Encadreur;
import mg.mef.stagemanagement.model.Soutenance;
import mg.mef.stagemanagement.model.StageParticipant;
import mg.mef.stagemanagement.model.Stagiaire;
import mg.mef.stagemanagement.model.StagiaireAcademique;
import mg.mef.stagemanagement.repository.SoutenanceRepository;
import mg.mef.stagemanagement.repository.StageParticipantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SoutenanceGestionService {
    private final DossierAccessService access;
    private final ParticipationRulesService rules;

    private final SoutenanceRepository soutenanceRepository;
    private final StageParticipantRepository stageParticipantRepository;

    public SoutenanceGestionService(
            SoutenanceRepository soutenanceRepository,
            StageParticipantRepository stageParticipantRepository
    , DossierAccessService access, ParticipationRulesService rules) {
        this.access = access; this.rules = rules;
        this.soutenanceRepository = soutenanceRepository;
        this.stageParticipantRepository = stageParticipantRepository;
    }

    public List<SoutenanceResponse> listerSoutenances() {
        return soutenanceRepository.findAll()
                .stream().filter(access::readDefense)
                .sorted(Comparator.comparing(Soutenance::getDateSoutenance))
                .map(this::toResponse)
                .toList();
    }

    public SoutenanceResponse trouverSoutenance(Integer id) {
        access.defense(id, false);
        Soutenance soutenance = soutenanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Soutenance introuvable"
                        )
                );

        return toResponse(soutenance);
    }

    @Transactional(readOnly = false)
    public SoutenanceResponse creerSoutenance(SoutenanceRequest request) {
        if (request != null && request.getStageParticipantId() != null) access.participation(request.getStageParticipantId(), true);
        verifierRequest(request);

        if (soutenanceRepository.existsByStageParticipant_Id(
                request.getStageParticipantId()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Une soutenance existe déjà pour cette participation"
            );
        }

        StageParticipant participant = stageParticipantRepository
                .findForUpdate(request.getStageParticipantId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Participation introuvable"
                        )
                );

        rules.lock(participant);
        Soutenance soutenance = new Soutenance();
        soutenance.setStageParticipant(participant);
        soutenance.setDateSoutenance(request.getDateSoutenance());
        soutenance.setLieu(nettoyerTexteOptionnel(request.getLieu(), 250));
        soutenance.setObservation(nettoyerTexteOptionnel(request.getObservation(), null));
        soutenance.setDateCreation(LocalDateTime.now());

        Soutenance enregistree = soutenanceRepository.save(soutenance);

        return toResponse(enregistree);
    }

    @Transactional(readOnly = false)
    public SoutenanceResponse modifierSoutenance(
            Integer id,
            SoutenanceRequest request
    ) {
        access.defense(id, true); if (request != null && request.getStageParticipantId() != null) access.participation(request.getStageParticipantId(), true);
        verifierRequest(request);

        Soutenance soutenance = soutenanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Soutenance introuvable"
                        )
                );

        rules.lock(soutenance.getStageParticipant());
        StageParticipant participant = stageParticipantRepository
                .findForUpdate(request.getStageParticipantId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Participation introuvable"
                        )
                );

        rules.lock(participant);
        soutenanceRepository.findByStageParticipant_Id(participant.getId())
                .ifPresent(existante -> {
                    if (!existante.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Une soutenance existe déjà pour cette participation"
                        );
                    }
                });

        soutenance.setStageParticipant(participant);
        soutenance.setDateSoutenance(request.getDateSoutenance());
        soutenance.setLieu(nettoyerTexteOptionnel(request.getLieu(), 250));
        soutenance.setObservation(nettoyerTexteOptionnel(request.getObservation(), null));

        Soutenance enregistree = soutenanceRepository.save(soutenance);

        return toResponse(enregistree);
    }

    private void verifierRequest(SoutenanceRequest request) {
        if (request == null
                || request.getStageParticipantId() == null
                || request.getDateSoutenance() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Les informations de soutenance sont incomplètes"
            );
        }
    }

    private SoutenanceResponse toResponse(Soutenance soutenance) {
        StageParticipant participant = soutenance.getStageParticipant();

        String stagiaireNom = null;
        String encadreurNom = null;

        if (participant != null && participant.getStagiaireAcademique() != null) {
            StagiaireAcademique academique = participant.getStagiaireAcademique();
            Stagiaire stagiaire = academique.getStagiaire();

            if (stagiaire != null) {
                stagiaireNom = stagiaire.getNom() + " " + stagiaire.getPrenoms();
            }
        }

        if (participant != null && participant.getEncadreur() != null) {
            Encadreur encadreur = participant.getEncadreur();
            encadreurNom = encadreur.getNom() + " " + encadreur.getPrenoms();
        }

        return new SoutenanceResponse(
                soutenance.getId(),
                participant != null ? participant.getId() : null,
                stagiaireNom,
                encadreurNom,
                soutenance.getDateSoutenance(),
                soutenance.getLieu(),
                soutenance.getObservation(),
                soutenance.getDateCreation()
        );
    }

    private String nettoyerTexteOptionnel(String valeur, Integer tailleMax) {
        if (valeur == null || valeur.trim().isEmpty()) {
            return null;
        }

        String texte = valeur.trim();

        if (tailleMax != null && texte.length() > tailleMax) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le texte est trop long"
            );
        }

        return texte;
    }
}