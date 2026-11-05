
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
import mg.mef.stagemanagement.model.RapportActivite;
import mg.mef.stagemanagement.repository.RapportActiviteRepository;
import mg.mef.stagemanagement.model.RapportActivite;
import mg.mef.stagemanagement.model.enums.AlertePriorite;
import mg.mef.stagemanagement.model.enums.AlerteStatut;
import mg.mef.stagemanagement.model.Encadreur;
import mg.mef.stagemanagement.repository.EncadreurRepository;
import mg.mef.stagemanagement.model.TypePieceJointe;
import mg.mef.stagemanagement.repository.TypePieceJointeRepository;
import mg.mef.stagemanagement.repository.StageParticipantFichierRepository;
import mg.mef.stagemanagement.repository.ParametresSuiviRepository;
import mg.mef.stagemanagement.model.ParametresSuivi;
import mg.mef.stagemanagement.model.Soutenance;
import mg.mef.stagemanagement.repository.SoutenanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDateTime;

@Service
public class AlerteDetectionService {

        private final AlerteRepository alerteRepository;
        private final StageParticipantRepository participationRepository;
        private final TypeAlerteRepository typeAlerteRepository;
        private final SeuilAlerteRepository seuilAlerteRepository;
        private final RapportActiviteRepository rapportActiviteRepository;
        private final CalculPeriodesRapportService calculPeriodesRapportService;
        private final ParametresSuiviRepository parametresSuiviRepository;
        private final TypePieceJointeRepository typePieceJointeRepository;
        private final StageParticipantFichierRepository stageParticipantFichierRepository;
        private final EncadreurRepository encadreurRepository;
        private final SoutenanceRepository soutenanceRepository;
        private final ParticipationRulesService participationRules;

        public AlerteDetectionService(
                AlerteRepository alerteRepository,
                StageParticipantRepository participationRepository,
                TypeAlerteRepository typeAlerteRepository,
                SeuilAlerteRepository seuilAlerteRepository,
                RapportActiviteRepository rapportActiviteRepository,
                CalculPeriodesRapportService calculPeriodesRapportService,
                ParametresSuiviRepository parametresSuiviRepository,
                TypePieceJointeRepository typePieceJointeRepository,
                StageParticipantFichierRepository stageParticipantFichierRepository,
                EncadreurRepository encadreurRepository,
                SoutenanceRepository soutenanceRepository,
                ParticipationRulesService participationRules
        ) {
                this.alerteRepository = alerteRepository;
                this.participationRepository = participationRepository;
                this.typeAlerteRepository = typeAlerteRepository;
                this.seuilAlerteRepository = seuilAlerteRepository;
                this.rapportActiviteRepository = rapportActiviteRepository;
                this.calculPeriodesRapportService = calculPeriodesRapportService;
                this.parametresSuiviRepository = parametresSuiviRepository;
                this.typePieceJointeRepository = typePieceJointeRepository;
                this.stageParticipantFichierRepository = stageParticipantFichierRepository;
                this.encadreurRepository = encadreurRepository;
                this.soutenanceRepository = soutenanceRepository;
                this.participationRules = participationRules;
        }

        public void detecterToutesLesAlertes() {

                detecterFinsStagesProches();

                detecterStagesDepasses();
        }

        @Transactional
        public int detecterStagesDepasses() {

                LocalDate aujourdHui = LocalDate.now(ZoneId.of("Indian/Antananarivo"));

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

        
        @Transactional
        public int detecterRapportsManquants() {

                LocalDate aujourdHui = LocalDate.now(
                        ZoneId.of("Indian/Antananarivo")
                );

                TypeAlerte type = typeAlerteRepository
                        .findByCodeIgnoreCaseAndActifTrue("RAPPORT_MANQUANT")
                        .orElseThrow(() -> new IllegalStateException(
                                "Type RAPPORT_MANQUANT introuvable ou inactif"
                        ));

                List<StageParticipant> participations =
                        participationRepository.findAll();

                Set<String> clesActives = new HashSet<>();
                int nouvellesAlertes = 0;

                for (StageParticipant participation : participations) {

                        String statutParticipation =
                                participation.getStatut().getCode();

                        // Ne pas demander de rapport pour un stage
                        // annulé ou abandonné.
                        if (AlerteClotureService.cloturee(participation)
                                || "ANNULEE".equals(statutParticipation)
                                || "ABANDONNEE".equals(statutParticipation)
                                || "PLANIFIEE".equals(statutParticipation)) {
                        continue;
                        }

                        List<RapportActivite> rapports =
                                rapportActiviteRepository
                                        .findByStageParticipant_Id(
                                                participation.getId()
                                        );

                        var periodes = calculPeriodesRapportService.calculer(
                                participation.getDateDebutStage(),
                                participation.getDateFinStage()
                        );

                        for (var periode : periodes) {

                        // Le lundi est encore autorisé pour la remise.
                        // L'alerte commence donc le mardi.
                        if (!aujourdHui.isAfter(periode.dateLimite())) {
                                continue;
                        }

                        boolean rapportRemis = rapports.stream().anyMatch(
                                rapport -> {

                                        boolean memePeriode =
                                                rapport.getDebutSemaine()
                                                        .equals(periode.debut())
                                                && rapport.getFinSemaine()
                                                        .equals(periode.fin());

                                        if (!memePeriode) {
                                        return false;
                                        }

                                        String statutRapport =
                                                rapport.getStatut().getCode();

                                        boolean statutRemis =
                                                "SOUMIS".equals(statutRapport)
                                                || "VALIDE".equals(statutRapport)
                                                || "A_CORRIGER".equals(statutRapport);

                                        return statutRemis
                                                && rapport.getDateSoumission() != null;
                                }
                        );

                        String cle = "RAPPORT_MANQUANT_PARTICIPATION_"
                                + participation.getId()
                                + "_"
                                + periode.debut();

                        if (rapportRemis) {
                                continue;
                        }

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

                        } else if (alerte.getStatut()
                                != AlerteStatut.A_TRAITER) {

                                alerte.setDateTraitement(null);
                                alerte.setTraitePar(null);
                        }

                        alerte.setStatut(AlerteStatut.A_TRAITER);
                        alerte.setPriorite(AlertePriorite.AVERTISSEMENT);
                        alerte.setDateEcheance(periode.dateLimite());

                        alerte.setMessage(
                                "Rapport hebdomadaire manquant pour la "
                                + "participation n°" + participation.getId()
                                + ", période du " + periode.debut()
                                + " au " + periode.fin()
                                + ". Date limite : " + periode.dateLimite()
                        );

                        alerteRepository.save(alerte);
                        }
                }

                // Résoudre les signalements dont la cause
                // n'est plus détectée.
                List<Alerte> alertesExistantes =
                        alerteRepository.findByTypeAlerte_Code(
                                "RAPPORT_MANQUANT"
                        );

                for (Alerte alerte : alertesExistantes) {

                        String cle = alerte.getCleDeduplication();

                        if (cle != null
                                && cle.startsWith("RAPPORT_MANQUANT_PARTICIPATION_")
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
        public int detecterValidationsEnRetard() {

                LocalDate aujourdHui = LocalDate.now(
                        ZoneId.of("Indian/Antananarivo")
                );

                ParametresSuivi parametres = parametresSuiviRepository
                        .findById((short) 1)
                        .orElseThrow(() -> new IllegalStateException(
                                "Paramètres de suivi introuvables"
                        ));

                int delai = parametres.getDelaiValidationRapportJours();

                TypeAlerte type = typeAlerteRepository
                        .findByCodeIgnoreCaseAndActifTrue("VALIDATION_EN_RETARD")
                        .orElseThrow(() -> new IllegalStateException(
                                "Type VALIDATION_EN_RETARD introuvable ou inactif"
                        ));

                List<RapportActivite> rapports =
                        rapportActiviteRepository.findAll();

                Set<String> clesActives = new HashSet<>();
                int nouvellesAlertes = 0;

                for (RapportActivite rapport : rapports) {

                        if (AlerteClotureService.cloturee(rapport.getStageParticipant())
                                || rapport.getStatut() == null
                                || !"SOUMIS".equals(rapport.getStatut().getCode())
                                || rapport.getDateSoumission() == null) {
                        continue;
                        }

                        LocalDate dateLimite = rapport.getDateSoumission()
                                .toLocalDate()
                                .plusDays(delai);

                        // La validation reste possible jusqu'à la fin
                        // du dernier jour autorisé.
                        if (!aujourdHui.isAfter(dateLimite)) {
                        continue;
                        }

                        String cle = "VALIDATION_EN_RETARD_RAPPORT_"
                                + rapport.getId();

                        clesActives.add(cle);

                        Alerte alerte = alerteRepository
                                .findByCleDeduplication(cle)
                                .orElse(null);

                        if (alerte == null) {

                        alerte = new Alerte();

                        alerte.setTypeAlerte(type);
                        alerte.setRapport(rapport);
                        alerte.setStageParticipant(
                                rapport.getStageParticipant()
                        );
                        alerte.setCleDeduplication(cle);
                        alerte.setDateDetection(LocalDateTime.now());

                        nouvellesAlertes++;

                        } else if (alerte.getStatut()
                                != AlerteStatut.A_TRAITER) {

                        alerte.setDateTraitement(null);
                        alerte.setTraitePar(null);
                        }

                        alerte.setStatut(AlerteStatut.A_TRAITER);
                        alerte.setPriorite(AlertePriorite.AVERTISSEMENT);
                        alerte.setDateEcheance(dateLimite);

                        alerte.setMessage(
                                "Validation en retard pour le rapport n° "
                                + rapport.getId()
                                + " de la participation n° "
                                + rapport.getStageParticipant().getId()
                                + ". Soumis le "
                                + rapport.getDateSoumission().toLocalDate()
                                + ". Date limite de validation : "
                                + dateLimite
                        );

                        alerteRepository.save(alerte);
                }

                // Résoudre les alertes dont la cause
                // n'est plus présente.
                List<Alerte> alertesExistantes =
                        alerteRepository.findByTypeAlerte_Code(
                                "VALIDATION_EN_RETARD"
                        );

                for (Alerte alerte : alertesExistantes) {

                        String cle = alerte.getCleDeduplication();

                        if (cle != null
                                && cle.startsWith("VALIDATION_EN_RETARD_RAPPORT_")
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
        public int detecterDossiersIncomplets() {

                TypeAlerte type = typeAlerteRepository
                        .findByCodeIgnoreCaseAndActifTrue("DOSSIER_INCOMPLET")
                        .orElseThrow(() -> new IllegalStateException(
                                "Type DOSSIER_INCOMPLET introuvable ou inactif"
                        ));

                List<TypePieceJointe> piecesObligatoires =
                        typePieceJointeRepository
                                .findByPourParticipationTrueAndObligatoireClotureTrueAndActifTrue();

                Set<String> clesActives = new HashSet<>();
                int nouvellesAlertes = 0;

                List<StageParticipant> participations =
                        participationRepository.findAll();

                for (StageParticipant participation : participations) {

                        if (AlerteClotureService.cloturee(participation)
                                || participation.getStatut() == null
                                || !"A_CLOTURER".equals(
                                        participation.getStatut().getCode())) {
                        continue;
                        }

                        List<String> documentsManquants = piecesObligatoires.stream()
                                .filter(piece ->
                                        !stageParticipantFichierRepository
                                                .existsByStageParticipant_IdAndTypePieceJointe_Id(
                                                        participation.getId(),
                                                        piece.getId()
                                                )
                                )
                                .map(TypePieceJointe::getLibelle)
                                .toList();

                        String cle = "DOSSIER_INCOMPLET_PARTICIPATION_"
                                + participation.getId();

                        var rapportsManquants = participationRules.missingClosureReports(participation);
                        if (documentsManquants.isEmpty() && rapportsManquants.isEmpty()) {
                        continue;
                        }

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
                        alerte.setDateTraitement(null);
                        alerte.setTraitePar(null);
                        }

                        alerte.setStatut(AlerteStatut.A_TRAITER);
                        alerte.setPriorite(AlertePriorite.AVERTISSEMENT);
                        alerte.setDateEcheance(participation.getDateFinStage());

                        alerte.setMessage(
                                "Dossier incomplet pour la participation n° "
                                + participation.getId()
                                + ". Documents obligatoires manquants : "
                                + String.join(", ", documentsManquants)
                                + ". Rapports obligatoires non valides : " + rapportsManquants.size()
                        );

                        alerteRepository.save(alerte);
                }

                // Clôturer les alertes dont la cause a disparu.
                List<Alerte> alertesExistantes =
                        alerteRepository.findByTypeAlerte_Code(
                                "DOSSIER_INCOMPLET"
                        );

                for (Alerte alerte : alertesExistantes) {

                        String cle = alerte.getCleDeduplication();

                        if (cle != null
                                && cle.startsWith("DOSSIER_INCOMPLET_PARTICIPATION_")
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

        
        @Transactional
        public int detecterEncadreursSurcharges() {

                TypeAlerte type = typeAlerteRepository
                        .findByCodeIgnoreCaseAndActifTrue("ENCADREUR_SURCHARGE")
                        .orElseThrow(() -> new IllegalStateException(
                                "Type ENCAdREUR_SURCHARGE introuvable ou inactif"
                        ));

                List<Object[]> resultats =
                        encadreurRepository.findEncadreursSurcharges();

                Set<String> clesActives = new HashSet<>();
                int nouvellesAlertes = 0;

                for (Object[] ligne : resultats) {

                        Integer encadreurId = ((Number) ligne[0]).intValue();
                        int capaciteMax = ((Number) ligne[1]).intValue();
                        int participationsActives = ((Number) ligne[2]).intValue();

                        Encadreur encadreur = encadreurRepository
                                .findById(encadreurId)
                                .orElseThrow(() -> new IllegalStateException(
                                        "Encadreur introuvable : " + encadreurId
                                ));

                        String cle = "ENCADREUR_SURCHARGE_"
                                + encadreurId;

                        clesActives.add(cle);

                        Alerte alerte = alerteRepository
                                .findByCleDeduplication(cle)
                                .orElse(null);

                        if (alerte == null) {

                        alerte = new Alerte();
                        alerte.setTypeAlerte(type);
                        alerte.setEncadreur(encadreur);
                        alerte.setCleDeduplication(cle);
                        alerte.setDateDetection(LocalDateTime.now());

                        nouvellesAlertes++;

                        } else if (alerte.getStatut()
                                != AlerteStatut.A_TRAITER) {

                        alerte.setDateTraitement(null);
                        alerte.setTraitePar(null);
                        }

                        alerte.setStatut(AlerteStatut.A_TRAITER);
                        alerte.setPriorite(AlertePriorite.AVERTISSEMENT);
                        alerte.setDateEcheance(null);

                        alerte.setMessage(
                                "Encadreur en surcharge : "
                                + encadreur.getNom() + " "
                                + encadreur.getPrenoms()
                                + ". Participations actives : "
                                + participationsActives
                                + ". Capacité maximale : "
                                + capaciteMax
                        );

                        alerteRepository.save(alerte);
                }

                // Résoudre les alertes dont la surcharge
                // n'est plus présente.
                List<Alerte> alertesExistantes =
                        alerteRepository.findByTypeAlerte_Code(
                                "ENCADREUR_SURCHARGE"
                        );

                for (Alerte alerte : alertesExistantes) {

                        String cle = alerte.getCleDeduplication();

                        if (cle != null
                                && cle.startsWith("ENCADREUR_SURCHARGE_")
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
        public int detecterSoutenancesProches() {

                TypeAlerte type = typeAlerteRepository
                        .findByCodeIgnoreCaseAndActifTrue("SOUTENANCE_PROCHE")
                        .orElseThrow(() -> new IllegalStateException(
                                "Type SOUTENANCE_PROCHE introuvable ou inactif"
                        ));

                List<Object[]> resultats =
                        soutenanceRepository.findSoutenancesProches();

                Set<String> clesActives = new HashSet<>();
                int nouvellesAlertes = 0;

                for (Object[] ligne : resultats) {

                        Integer soutenanceId = ((Number) ligne[0]).intValue();
                        Integer participationId = ((Number) ligne[1]).intValue();
                        int joursRestants = ((Number) ligne[3]).intValue();
                        String niveau = ligne[4].toString();

                        Soutenance soutenance = soutenanceRepository
                                .findById(soutenanceId)
                                .orElseThrow(() -> new IllegalStateException(
                                        "Soutenance introuvable : " + soutenanceId
                                ));

                        String cle = "SOUTENANCE_PROCHE_" + soutenanceId;
                        if (AlerteClotureService.cloturee(soutenance.getStageParticipant())) {
                                continue;
                        }
                        clesActives.add(cle);

                        Alerte alerte = alerteRepository
                                .findByCleDeduplication(cle)
                                .orElse(null);

                        if (alerte == null) {
                        alerte = new Alerte();
                        alerte.setTypeAlerte(type);
                        alerte.setSoutenance(soutenance);
                        alerte.setStageParticipant(
                                soutenance.getStageParticipant()
                        );
                        alerte.setCleDeduplication(cle);
                        alerte.setDateDetection(LocalDateTime.now());

                        nouvellesAlertes++;
                        } else if (alerte.getStatut() != AlerteStatut.A_TRAITER) {
                        alerte.setDateTraitement(null);
                        alerte.setTraitePar(null);
                        }

                        AlertePriorite priorite = switch (niveau) {
                        case "URGENT" -> AlertePriorite.URGENT;
                        case "AVERTISSEMENT" -> AlertePriorite.AVERTISSEMENT;
                        default -> AlertePriorite.INFORMATION;
                        };

                        alerte.setStatut(AlerteStatut.A_TRAITER);
                        alerte.setPriorite(priorite);
                        alerte.setDateEcheance(
                                soutenance.getDateSoutenance().toLocalDate()
                        );

                        alerte.setMessage(
                                "Soutenance proche pour la participation n° "
                                + participationId
                                + " : dans " + joursRestants + " jour(s). "
                                + "Niveau : " + niveau
                        );

                        alerteRepository.save(alerte);
                }

                // Expirer les alertes des soutenances qui ne
                // sont plus dans la fenêtre de surveillance.
                List<Alerte> alertesExistantes =
                        alerteRepository.findByTypeAlerte_Code(
                                "SOUTENANCE_PROCHE"
                        );

                for (Alerte alerte : alertesExistantes) {

                        String cle = alerte.getCleDeduplication();

                        if (cle != null
                                && cle.startsWith("SOUTENANCE_PROCHE_")
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
