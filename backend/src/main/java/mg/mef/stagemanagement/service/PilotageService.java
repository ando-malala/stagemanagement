package mg.mef.stagemanagement.service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import mg.mef.stagemanagement.model.*;
import mg.mef.stagemanagement.model.enums.AlerteStatut;
import mg.mef.stagemanagement.repository.*;
import mg.mef.stagemanagement.dto.soutenance.SoutenanceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PilotageService {
    public record Priorite(Integer participationId, Integer stagiaireId, String stagiaire, long nombreAlertes,
            long score, String priorite, String situation, String actionRecommandee) {}
    public record Dossier(Integer participationId, String stagiaire, String stage, String statut, LocalDate dateFin,
            long joursRestants, long documentsManquants, long rapportsAttendus, long rapportsSoumis, long rapportsValides,
            long rapportsManquants) {}
    public record Charge(Integer encadreurId, String encadreur, Integer capaciteMax, long participationsActives, boolean surcharge) {}
    public record Pilotage(LocalDate dateCalcul, List<Dossier> dossiers, List<Priorite> priorites,
            List<Charge> charges, List<SoutenanceResponse> soutenancesProchaines) {}
    private final DossierAccessService access;
    private final StageParticipantRepository participations;
    private final AlerteRepository alertes;
    private final EncadreurRepository encadreurs;
    private final DocumentCatalogueService documents;
    private final SuiviParticipationService suivi;
    private final SoutenanceGestionService soutenances;
    public PilotageService(DossierAccessService access, StageParticipantRepository participations, AlerteRepository alertes,
            EncadreurRepository encadreurs, DocumentCatalogueService documents, SuiviParticipationService suivi,
            SoutenanceGestionService soutenances) {
        this.access = access; this.participations = participations; this.alertes = alertes; this.encadreurs = encadreurs;
        this.documents = documents; this.suivi = suivi; this.soutenances = soutenances;
    }
    public Pilotage pilotage() {
        LocalDate today = LocalDate.now(ZoneId.of("Indian/Antananarivo"));
        var dossiers = participations.findAll().stream().filter(access::readParticipation).map(p -> {
            var periods = suivi.periodes(p.getId());
            return new Dossier(p.getId(), nom(p), p.getStage().getLibelle(), p.getStatut().getCode(), p.getDateFinStage(),
                    java.time.temporal.ChronoUnit.DAYS.between(today, p.getDateFinStage()),
                    documents.obligatoires(p.getId()).stream().filter(d -> !d.present()).count(),
                    periods.stream().filter(r -> r.exigible() && !r.fin().isAfter(today)).count(),
                    periods.stream().filter(SuiviParticipationService.Periode::soumise).count(),
                    periods.stream().filter(SuiviParticipationService.Periode::validee).count(),
                    periods.stream().filter(SuiviParticipationService.Periode::manquante).count());
        }).sorted(Comparator.comparing(Dossier::dateFin).thenComparing(Dossier::participationId)).toList();
        var charges = encadreurs.findAll().stream().filter(access::readMentor).map(e -> {
            var c = suivi.charge(e.getId());
            return new Charge(e.getId(), e.getNom() + " " + e.getPrenoms(), c.capaciteMax(), c.participationsActives(), c.surcharge());
        }).toList();
        var defenses = soutenances.listerSoutenances().stream()
                .filter(s -> !s.getDateSoutenance().toLocalDate().isBefore(today) && !s.getDateSoutenance().toLocalDate().isAfter(today.plusDays(30)))
                .sorted(Comparator.comparing(SoutenanceResponse::getDateSoutenance)).toList();
        return new Pilotage(today, dossiers, priorites(), charges, defenses);
    }
    public List<Priorite> priorites() {
        // Same weights/thresholds as vue_actions_prioritaires, AFTER filtering each alert and all its targets.
        // Filtering only the final SQL view would disclose messages on an alert linked to multiple departments.
        Map<Integer, List<Alerte>> groups = alertes.findByStatutOrderByDateDetectionDesc(AlerteStatut.A_TRAITER).stream()
                .filter(access::readAlert).filter(a -> participation(a) != null)
                .collect(Collectors.groupingBy(a -> participation(a).getId()));
        return groups.values().stream().map(rows -> {
            var p = participation(rows.get(0));
            long score = rows.stream().mapToLong(a -> a.getTypeAlerte().getPoidsPriorite()).sum();
            String priority = score >= 50 || rows.stream().anyMatch(a -> "URGENT".equals(a.getPriorite().name())) ? "URGENTE"
                    : score >= 20 || rows.stream().anyMatch(a -> "AVERTISSEMENT".equals(a.getPriorite().name())) ? "HAUTE" : "NORMALE";
            String situation = rows.stream().map(a -> a.getMessage() == null ? a.getTypeAlerte().getLibelle() : a.getMessage()).distinct().sorted().collect(Collectors.joining(" + "));
            String action = rows.stream().map(a -> a.getTypeAlerte().getActionRecommandee()).filter(Objects::nonNull).distinct().sorted().collect(Collectors.joining(" / "));
            return new Priorite(p.getId(), p.getStagiaireAcademique().getStagiaire().getId(), nom(p), rows.size(), score, priority, situation, action);
        }).sorted(Comparator.comparingInt((Priorite p) -> List.of("URGENTE", "HAUTE", "NORMALE").indexOf(p.priorite()))
                .thenComparing(Comparator.comparingLong(Priorite::score).reversed()).thenComparing(Priorite::participationId)).toList();
    }
    private static StageParticipant participation(Alerte a) {
        return a.getStageParticipant() != null ? a.getStageParticipant() : a.getRapport() != null ? a.getRapport().getStageParticipant()
                : a.getSoutenance() != null ? a.getSoutenance().getStageParticipant() : null;
    }
    private static String nom(StageParticipant p) {
        var s = p.getStagiaireAcademique().getStagiaire(); return s.getNom() + " " + s.getPrenoms();
    }
}
