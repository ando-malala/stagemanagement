
package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.Alerte;
import mg.mef.stagemanagement.model.StageParticipant;
import mg.mef.stagemanagement.model.TypeAlerte;
import mg.mef.stagemanagement.model.enums.AlertePriorite;
import mg.mef.stagemanagement.model.enums.AlerteStatut;
import mg.mef.stagemanagement.repository.AlerteRepository;
import mg.mef.stagemanagement.repository.StageParticipantRepository;
import mg.mef.stagemanagement.repository.TypeAlerteRepository;
import mg.mef.stagemanagement.model.SeuilAlerte;
import mg.mef.stagemanagement.repository.SeuilAlerteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AlerteDetectionService {

        private final AlerteRepository alerteRepository;
        private final StageParticipantRepository participationRepository;
        private final TypeAlerteRepository typeAlerteRepository;
        private final SeuilAlerteRepository seuilAlerteRepository;

        public AlerteDetectionService(
                AlerteRepository alerteRepository,
                StageParticipantRepository participationRepository,
                TypeAlerteRepository typeAlerteRepository,
                SeuilAlerteRepository seuilAlerteRepository
        ) {
                this.alerteRepository = alerteRepository;
                this.participationRepository = participationRepository;
                this.typeAlerteRepository = typeAlerteRepository;
                this.seuilAlerteRepository = seuilAlerteRepository;
        }

        @Transactional
        public int detecterStagesDepasses() {

                LocalDate aujourdHui = LocalDate.now();

                TypeAlerte type = typeAlerteRepository
                        .findByCodeIgnoreCaseAndActifTrue("STAGE_DEPASSE")
                        .orElseThrow(() -> new IllegalStateException(
                                "Type d'alerte STAGE_DEPASSE introuvable ou inactif"
                        ));

                List<StageParticipant> participations =
                        participationRepository.findStagesDepassesNonClotures(
                                aujourdHui
                        );

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
                        alerte.setTypeAlerte(type);
                        alerte.setStageParticipant(participation);
                        alerte.setPriorite(AlertePriorite.URGENT);
                        alerte.setStatut(AlerteStatut.A_TRAITER);
                        alerte.setDateDetection(LocalDateTime.now());
                        alerte.setCleDeduplication(cle);

                        nouvellesAlertes++;

                } else if (alerte.getStatut() != AlerteStatut.A_TRAITER) {

                        alerte.setStatut(AlerteStatut.A_TRAITER);
                        alerte.setDateTraitement(null);
                        alerte.setTraitePar(null);
                }

                long joursRetard = ChronoUnit.DAYS.between(
                        participation.getDateFinStage(),
                        aujourdHui
                );

                alerte.setDateEcheance(participation.getDateFinStage());

                alerte.setMessage(
                        "La participation n°" + participation.getId()
                        + " a dépassé sa date de fin de "
                        + joursRetard + " jour(s) sans clôture."
                );

                alerteRepository.save(alerte);
                }

                List<Alerte> anciennesAlertes =
                        alerteRepository.findByTypeAlerte_Code("STAGE_DEPASSE");

                for (Alerte alerte : anciennesAlertes) {

                String cle = alerte.getCleDeduplication();

                if (cle != null
                        && cle.startsWith("STAGE_DEPASSE_PARTICIPATION_")
                        && !clesActives.contains(cle)
                        && alerte.getStatut() == AlerteStatut.A_TRAITER) {

                        alerte.setStatut(AlerteStatut.TRAITEE);
                        alerte.setDateTraitement(LocalDateTime.now());
                        alerte.setTraitePar(null);

                        alerteRepository.save(alerte);
                }
                }

                return nouvellesAlertes;
        }

    
        @Transactional
        public int detecterFinsStagesProches() {

                LocalDate aujourdHui = LocalDate.now(
                        ZoneId.of("Indian/Antananarivo")
                );

                TypeAlerte type = typeAlerteRepository
                        .findByCodeIgnoreCaseAndActifTrue("FIN_STAGE_PROCHE")
                        .orElseThrow(() -> new IllegalStateException(
                                "Type FIN_STAGE_PROCHE introuvable ou inactif"
                        ));

                List<SeuilAlerte> seuils = seuilAlerteRepository
                        .findByTypeAlerte_CodeAndActifTrueOrderByJoursAvantAsc(
                                "FIN_STAGE_PROCHE"
                        );

                if (seuils.isEmpty()) {
                        throw new IllegalStateException(
                                "Aucun seuil actif pour FIN_STAGE_PROCHE"
                        );
                }

                int joursMaximum = seuils.stream()
                        .mapToInt(SeuilAlerte::getJoursAvant)
                        .max()
                        .orElseThrow();

                List<StageParticipant> participations =
                        participationRepository.findStagesProchesDeFin(
                                aujourdHui,
                                aujourdHui.plusDays(joursMaximum)
                        );

                Set<String> clesActives = new HashSet<>();
                int nouvellesAlertes = 0;

                for (StageParticipant participation : participations) {

                        long joursRestants = ChronoUnit.DAYS.between(
                                aujourdHui,
                                participation.getDateFinStage()
                        );

                        SeuilAlerte seuilApplicable = seuils.stream()
                                .filter(s -> joursRestants <= s.getJoursAvant())
                                .min(Comparator.comparingInt(
                                        SeuilAlerte::getJoursAvant
                                ))
                                .orElseThrow();

                        String cle = "FIN_STAGE_PROCHE_PARTICIPATION_"
                                + participation.getId();

                        clesActives.add(cle);

                        Alerte alerte = alerteRepository
                                .findByCleDeduplication(cle)
                                .orElse(null);

                        if (alerte == null) {
                        alerte = new Alerte();
                        alerte.setTypeAlerte(type);
                        alerte.setStageParticipant(participation);
                        alerte.setCleDeduplication(cle);
                        alerte.setDateDetection(LocalDateTime.now());
                        nouvellesAlertes++;
                        } else if (alerte.getStatut() != AlerteStatut.A_TRAITER) {
                        alerte.setStatut(AlerteStatut.A_TRAITER);
                        alerte.setDateTraitement(null);
                        alerte.setTraitePar(null);
                        }

                        alerte.setPriorite(seuilApplicable.getPriorite());
                        alerte.setDateEcheance(participation.getDateFinStage());
                        alerte.setStatut(AlerteStatut.A_TRAITER);

                        alerte.setMessage(
                                "La participation n°" + participation.getId()
                                + " se termine dans " + joursRestants
                                + " jour(s). Préparer la clôture du stage."
                        );

                        alerteRepository.save(alerte);
                }

                List<Alerte> alertesExistantes =
                        alerteRepository.findByTypeAlerte_Code(
                                "FIN_STAGE_PROCHE"
                        );

                for (Alerte alerte : alertesExistantes) {

                        String cle = alerte.getCleDeduplication();

                        if (cle != null
                                && cle.startsWith("FIN_STAGE_PROCHE_PARTICIPATION_")
                                && !clesActives.contains(cle)
                                && alerte.getStatut() == AlerteStatut.A_TRAITER) {

                        alerte.setStatut(AlerteStatut.EXPIREE);
                        alerte.setDateTraitement(LocalDateTime.now());
                        alerte.setTraitePar(null);

                        alerteRepository.save(alerte);
                        }
                }

                return nouvellesAlertes;
        }

}
