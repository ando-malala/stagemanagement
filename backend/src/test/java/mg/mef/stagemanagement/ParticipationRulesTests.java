package mg.mef.stagemanagement;

import mg.mef.stagemanagement.service.*;
import mg.mef.stagemanagement.repository.*;
import mg.mef.stagemanagement.model.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipationRulesTests {
    final RapportActiviteRepository reports = mock(RapportActiviteRepository.class);
    final StageParticipantRepository participations = mock(StageParticipantRepository.class);
    final StagiaireRepository trainees = mock(StagiaireRepository.class);
    final ParticipationRulesService rules = new ParticipationRulesService(participations, trainees, reports, new CalculPeriodesRapportService());
    StageParticipant p() {
        var p = new StageParticipant(); p.setId(1);
        p.setDateDebutStage(LocalDate.of(2026,1,1)); p.setDateFinStage(LocalDate.of(2026,1,4));
        var s = new Statut(); s.setCode("EN_COURS"); p.setStatut(s); return p;
    }
    @Test void terminalDossiersCannotBeEdited() {
        for (String code : List.of("CLOTUREE", "ANNULEE", "ABANDONNEE")) {
            var p=p(); p.getStatut().setCode(code); assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> rules.mutable(p));
        }
        var p=p(); p.setDateCloture(LocalDateTime.now()); assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> rules.mutable(p));
    }
    @Test void reportMustMatchTheCalculatedPeriod() {
        var p=p(); assertDoesNotThrow(() -> rules.reportPeriod(p,p.getDateDebutStage(),p.getDateFinStage()));
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> rules.reportPeriod(p,p.getDateDebutStage().plusDays(1),p.getDateFinStage()));
    }
    @Test void closureRequiresValidatedCompletedPeriods() {
        var p=p(); when(reports.findByStageParticipant_Id(1)).thenReturn(List.of());
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> rules.closureReports(p));
        var r=new RapportActivite(); r.setDebutSemaine(p.getDateDebutStage()); r.setFinSemaine(p.getDateFinStage());
        r.setDateSoumission(LocalDateTime.now()); var s=new Statut(); s.setCode("SOUMIS"); r.setStatut(s);
        when(reports.findByStageParticipant_Id(1)).thenReturn(List.of(r));
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> rules.closureReports(p));
        s.setCode("VALIDE"); assertDoesNotThrow(() -> rules.closureReports(p));
        r.setDateSoumission(null); assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> rules.closureReports(p));
    }
    @Test void overlapChecksUsePersonAcrossAcademicProfiles() {
        var t=new Stagiaire(); t.setId(9); var a=new StagiaireAcademique(); a.setId(30); a.setStagiaire(t);
        when(trainees.findForUpdate(9)).thenReturn(Optional.of(t));
        when(participations.countOverlapping(eq(9),eq(-1),any(),any())).thenReturn(1L);
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> rules.dates(null,a,LocalDate.of(2026,1,1),LocalDate.of(2026,1,4)));
        verify(trainees).findForUpdate(9);
    }
}
