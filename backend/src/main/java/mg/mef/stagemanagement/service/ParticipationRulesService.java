package mg.mef.stagemanagement.service;

import java.time.*;
import java.util.*;
import mg.mef.stagemanagement.model.*;
import mg.mef.stagemanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ParticipationRulesService {
    @jakarta.persistence.PersistenceContext private jakarta.persistence.EntityManager entityManager;
    public void lock(StageParticipant p) { entityManager.refresh(p, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE); mutable(p); }
    public void lockEvaluation(EvaluationFinale e) { entityManager.refresh(e, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE); lock(e.getStageParticipant()); }
    public void refreshReport(RapportActivite report) { entityManager.refresh(report); }
    public void mentorChange(StageParticipant current, Integer mentorId) {
        if (!Objects.equals(current.getEncadreur().getId(), mentorId)
                && !entityManager.createQuery("select e.id from EvaluationFinale e where e.stageParticipant.id = :id and e.encadreur.id <> :mentor", Integer.class)
                    .setParameter("id", current.getId()).setParameter("mentor", mentorId).setMaxResults(1).getResultList().isEmpty())
            conflict("Une évaluation existe avec l'encadreur actuel : cette réaffectation rendrait le dossier incohérent");
    }
    private final StageParticipantRepository participations;
    private final StagiaireRepository trainees;
    private final RapportActiviteRepository reports;
    private final CalculPeriodesRapportService calendar;
    public ParticipationRulesService(StageParticipantRepository participations, StagiaireRepository trainees,
            RapportActiviteRepository reports, CalculPeriodesRapportService calendar) {
        this.participations = participations; this.trainees = trainees; this.reports = reports; this.calendar = calendar;
    }
    public static void conflict(String message) { throw new ResponseStatusException(HttpStatus.CONFLICT, message); }
    public void mutable(StageParticipant p) {
        if (p.getDateCloture() != null || Set.of("CLOTUREE", "ANNULEE", "ABANDONNEE").contains(p.getStatut().getCode()))
            conflict("Cette participation est terminee et ne peut plus etre modifiee");
    }
    public void dates(Integer id, StagiaireAcademique academic, LocalDate start, LocalDate end) {
        // Serialize assignments of the same person, including different academic profiles.
        trainees.findForUpdate(academic.getStagiaire().getId()).orElseThrow();
        if (participations.countOverlapping(academic.getStagiaire().getId(), id == null ? -1 : id, start, end) > 0)
            conflict("Une autre participation de ce stagiaire chevauche cette periode");
        if (id != null) {
            var periods = calendar.calculer(start, end);
            for (var r : reports.findByStageParticipant_Id(id)) {
                if (periods.stream().noneMatch(p -> p.debut().equals(r.getDebutSemaine()) && p.fin().equals(r.getFinSemaine())))
                    conflict("Ces dates invalideraient une periode de rapport existante");
            }
        }
    }
    public void reportPeriod(StageParticipant p, LocalDate start, LocalDate end) {
        mutable(p);
        if (calendar.calculer(p.getDateDebutStage(), p.getDateFinStage()).stream()
                .noneMatch(period -> period.debut().equals(start) && period.fin().equals(end)))
            conflict("Choisissez une periode de rapport calculee par le serveur");
    }
    public void closureReports(StageParticipant p) {
        var missing = missingClosureReports(p);
        if (!missing.isEmpty()) {
            var period = missing.get(0);
            conflict("Rapport obligatoire non valide pour la periode du " + period.debut() + " au " + period.fin());
        }
    }
    public List<CalculPeriodesRapportService.PeriodeRapport> missingClosureReports(StageParticipant p) {
        var rows = reports.findByStageParticipant_Id(p.getId());
        var today = LocalDate.now(ZoneId.of("Indian/Antananarivo"));
        // Completed periods are required, even before their submission deadline.
        return calendar.calculer(p.getDateDebutStage(), p.getDateFinStage()).stream()
                .filter(period -> !period.fin().isAfter(today))
                .filter(period -> rows.stream().noneMatch(r -> period.debut().equals(r.getDebutSemaine())
                    && period.fin().equals(r.getFinSemaine()) && "VALIDE".equals(r.getStatut().getCode())
                    && r.getDateSoumission() != null)).toList();
    }
}
