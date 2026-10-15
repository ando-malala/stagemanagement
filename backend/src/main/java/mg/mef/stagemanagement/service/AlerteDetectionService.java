package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.Alerte;
import mg.mef.stagemanagement.model.StageParticipant;
import mg.mef.stagemanagement.model.TypeAlerte;
import mg.mef.stagemanagement.model.enums.AlertePriorite;
import mg.mef.stagemanagement.model.enums.AlerteStatut;

import mg.mef.stagemanagement.repository.AlerteRepository;
import mg.mef.stagemanagement.repository.StageParticipantRepository;
import mg.mef.stagemanagement.repository.TypeAlerteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AlerteDetectionService {

    private final AlerteRepository alerteRepository;
    private final StageParticipantRepository stageParticipantRepository;
    private final TypeAlerteRepository typeAlerteRepository;

    public AlerteDetectionService(
            AlerteRepository alerteRepository,
            StageParticipantRepository stageParticipantRepository,
            TypeAlerteRepository typeAlerteRepository
    ) {
        this.alerteRepository = alerteRepository;
        this.stageParticipantRepository = stageParticipantRepository;
        this.typeAlerteRepository = typeAlerteRepository;
    }

    @Transactional
    public int detecterStagesDepasses() {

        LocalDate aujourdHui = LocalDate.now();

        TypeAlerte typeAlerte = typeAlerteRepository
                .findByCodeIgnoreCaseAndActifTrue("STAGE_DEPASSE")
                .orElseThrow(() -> new IllegalStateException(
                        "Le type d'alerte STAGE_DEPASSE est introuvable ou inactif"
                ));

        List<StageParticipant> participations =
                stageParticipantRepository
                        .findStagesDepassesNonClotures(aujourdHui);

        Set<String> clesActives = new HashSet<>();
        int nouvellesAlertes = 0;

        for (StageParticipant participation : participations) {

            String cle = "STAGE_DEPASSE_PARTICIPATION_"
                    + participation.getId();

            clesActives.add(cle);

            Alerte alerte = alerteRepository
                    .findByCleDeduplication(cle)
                    .orElse(null);

            if (alerte == null) {

                alerte = new Alerte();

                alerte.setTypeAlerte(typeAlerte);
                alerte.setStageParticipant(participation);
                alerte.setPriorite(AlertePriorite.URGENT);
                alerte.setStatut(AlerteStatut.A_TRAITER);
                alerte.setDateDetection(LocalDateTime.now());
                alerte.setCleDeduplication(cle);

                nouvellesAlertes++;
            } else if (alerte.getStatut() != AlerteStatut.A_TRAITER) {

                // La condition existe toujours : l'anomalie doit
                // redevenir visible même si elle avait été traitée.
                alerte.setStatut(AlerteStatut.A_TRAITER);
                alerte.setDateTraitement(null);
                alerte.setTraitePar(null);
            }

            long joursRetard = java.time.temporal.ChronoUnit.DAYS
                    .between(participation.getDateFinStage(), aujourdHui);

            alerte.setDateEcheance(participation.getDateFinStage());

            alerte.setMessage(
                    "La participation n°" + participation.getId()
                    + " a dépassé sa date de fin de "
                    + joursRetard
                    + " jour(s) sans clôture effective."
            );

            alerteRepository.save(alerte);
        }

        // Résoudre automatiquement les anciennes alertes dont
        // la condition n'est plus vraie.
        List<Alerte> alertesExistantes =
                alerteRepository.findByTypeAlerte_Code("STAGE_DEPASSE");

        for (Alerte alerte : alertesExistantes) {

            String cle = alerte.getCleDeduplication();

            if (cle != null
                    && cle.startsWith("STAGE_DEPASSE_PARTICIPATION_")
                    && !clesActives.contains(cle)
                    && alerte.getStatut() == AlerteStatut.A_TRAITER) {

                alerte.setStatut(AlerteStatut.TRAITEE);
                alerte.setDateTraitement(LocalDateTime.now());

                alerteRepository.save(alerte);
            }
        }

        return nouvellesAlertes;
    }
}