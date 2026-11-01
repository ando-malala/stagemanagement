package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.*;
import mg.mef.stagemanagement.repository.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class SuiviParticipationService {
    private final DossierAccessService access;
    public record Historique(Integer id, String ancienStatut, String statut, LocalDateTime dateModification, String motif, Integer responsableId) {}
    public record Evenement(Integer id, String code, String libelle, LocalDateTime dateEvenement, LocalDate dateEffet,
            LocalDate ancienneDateFin, LocalDate nouvelleDateFin, String motif, Integer responsableId) {}
    public record Periode(LocalDate debut, LocalDate fin, LocalDate dateLimite, Integer rapportId, String statutRapport,
            boolean soumise, boolean validee, boolean exigible, boolean manquante) {}
    public record Action(String code, String label, String statutCible, boolean dateEffet) {}
    public record Charge(Integer encadreurId, Integer capaciteMax, Long participationsActives, boolean surcharge) {}
    private final StageParticipantRepository participations;
    private final StageParticipantEvenementRepository evenements;
    private final StageParticipantStatutHistoriqueRepository historiques;
    private final RapportActiviteRepository rapports;
    private final EncadreurRepository encadreurs;
    private final CalculPeriodesRapportService calcul;
    private final ReferentielWorkflowService workflow;
    public SuiviParticipationService(StageParticipantRepository participations, StageParticipantEvenementRepository evenements,
            StageParticipantStatutHistoriqueRepository historiques, RapportActiviteRepository rapports,
            EncadreurRepository encadreurs, CalculPeriodesRapportService calcul, ReferentielWorkflowService workflow, DossierAccessService access) {
        this.access = access;
        this.participations = participations; this.evenements = evenements; this.historiques = historiques;
        this.rapports = rapports; this.encadreurs = encadreurs; this.calcul = calcul; this.workflow = workflow;
    }
    private StageParticipant participation(Integer id) {
        return participations.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participation introuvable"));
    }
    public List<Historique> historiques(Integer id) {
        access.participation(id, false);
        participation(id);
        return historiques.findByStageParticipant_IdOrderByDateModificationAscIdAsc(id).stream()
                .map(h -> new Historique(h.getId(), h.getAncienStatut() == null ? null : h.getAncienStatut().getCode(), h.getStatut().getCode(),
                        h.getDateModification(), h.getMotif(), h.getResponsable() == null ? null : h.getResponsable().getId())).toList();
    }
    public List<Evenement> evenements(Integer id) {
        access.participation(id, false);
        participation(id);
        return evenements.findByStageParticipant_IdOrderByDateEvenementAscIdAsc(id).stream()
                .map(e -> new Evenement(e.getId(), e.getTypeEvenement().getCode(), e.getTypeEvenement().getLibelle(), e.getDateEvenement(),
                        e.getDateEffet(), e.getAncienneDateFin(), e.getNouvelleDateFin(), e.getMotif(), e.getResponsable() == null ? null : e.getResponsable().getId())).toList();
    }
    public List<Periode> periodes(Integer id) {
        access.participation(id, false);
        return periodes(participation(id), rapports.findByStageParticipant_Id(id), LocalDate.now(ZoneId.of("Indian/Antananarivo")));
    }
    // Same calendar and definition of a submitted report as the existing detector.
    public List<Periode> periodes(StageParticipant p, List<RapportActivite> rows, LocalDate today) {
        boolean suivi = !List.of("PLANIFIEE", "ANNULEE", "ABANDONNEE").contains(p.getStatut().getCode());
        return calcul.calculer(p.getDateDebutStage(), p.getDateFinStage()).stream().map(periode -> {
            RapportActivite rapport = rows.stream().filter(r -> periode.debut().equals(r.getDebutSemaine()) && periode.fin().equals(r.getFinSemaine()))
                    .findFirst().orElse(null);
            String statut = rapport == null ? null : rapport.getStatut().getCode();
            boolean remis = rapport != null && rapport.getDateSoumission() != null && Set.of("SOUMIS", "VALIDE", "A_CORRIGER").contains(statut);
            return new Periode(periode.debut(), periode.fin(), periode.dateLimite(), rapport == null ? null : rapport.getId(), statut,
                    remis, remis && "VALIDE".equals(statut), suivi, suivi && today.isAfter(periode.dateLimite()) && !remis);
        }).toList();
    }
    public Charge charge(Integer id) {
        access.mentor(id, false);
        if (!encadreurs.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Encadreur introuvable");
        var c = encadreurs.findCharge(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Charge indisponible"));
        if (c.getCapaciteMax() == null) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Capacite non configuree");
        long count = participations.findByEncadreur_Id(id).stream().filter(access::readParticipation)
                .filter(p -> Set.of("EN_COURS", "A_CLOTURER").contains(p.getStatut().getCode())).count();
        return new Charge(c.getEncadreurId(), c.getCapaciteMax(), count, count > c.getCapaciteMax());
    }
    public List<Action> actions(Integer id) {
        access.participation(id, false);
        var p = participation(id);
        if (p.getDateCloture() != null || access.traineeOnly() || !access.manageParticipation(p)) return List.of();
        var actifs = workflow.evenements().stream().map(ReferentielWorkflowService.Reference::code).toList();
        var actions = new ArrayList<Action>();
        for (var t : workflow.transitions("PARTICIPATION", p.getStatut().getCode())) {
            String event = switch (t.cible()) {
                case "SUSPENDUE" -> "SUSPENSION";
                case "EN_COURS" -> "SUSPENDUE".equals(p.getStatut().getCode()) ? "REPRISE" : "";
                case "ABANDONNEE" -> "ABANDON";
                case "ANNULEE" -> "ANNULATION";
                case "CLOTUREE" -> "CLOTURE";
                default -> "";
            };
            if (!event.isEmpty() && !actifs.contains(event)) continue;
            String operation = switch(event) {
                case "SUSPENSION" -> "suspendre"; case "REPRISE" -> "reprendre";
                case "ABANDON" -> "abandonner"; case "ANNULATION" -> "annuler"; case "CLOTURE" -> "cloturer";
                default -> "statut";
            };
            String label = switch(operation) {
                case "suspendre" -> "Suspendre"; case "reprendre" -> "Reprendre";
                case "abandonner" -> "Abandonner"; case "annuler" -> "Annuler la participation";
                case "cloturer" -> "Clôturer";
                default -> "A_CLOTURER".equals(t.cible()) ? "Préparer la clôture"
                        : "EN_COURS".equals(t.cible()) ? ("PLANIFIEE".equals(p.getStatut().getCode()) ? "Démarrer" : "Remettre en cours")
                        : "Passer à « " + t.libelle() + " »";
            };
            actions.add(new Action(operation, label, t.cible(), Set.of("SUSPENSION", "REPRISE").contains(event)));
        }
        if (actifs.contains("PROLONGATION") && List.of("PLANIFIEE", "EN_COURS", "SUSPENDUE", "A_CLOTURER").contains(p.getStatut().getCode()))
            actions.add(new Action("prolonger", "Prolonger", null, false));
        return actions;
    }
}
