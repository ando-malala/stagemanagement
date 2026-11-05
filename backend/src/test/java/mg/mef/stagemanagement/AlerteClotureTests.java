package mg.mef.stagemanagement;

import java.time.LocalDateTime;
import java.util.List;
import mg.mef.stagemanagement.model.*;
import mg.mef.stagemanagement.model.enums.AlerteStatut;
import mg.mef.stagemanagement.repository.AlerteRepository;
import mg.mef.stagemanagement.service.AlerteClotureService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlerteClotureTests {
    @Test void excludesClosedParticipationThroughEveryAlertLink() {
        var closed = new StageParticipant();
        var status = new Statut(); status.setCode("CLOTUREE"); closed.setStatut(status);
        var direct = new Alerte(); direct.setStageParticipant(closed);
        var report = new RapportActivite(); report.setStageParticipant(closed);
        var indirect = new Alerte(); indirect.setRapport(report);
        var defense = new Soutenance(); defense.setStageParticipant(closed);
        var defenseAlert = new Alerte(); defenseAlert.setSoutenance(defense);
        for (var alert : List.of(direct, indirect, defenseAlert)) {
            assertFalse(AlerteClotureService.encorePertinente(alert));
        }
        status.setCode("A_CLOTURER");
        closed.setDateCloture(LocalDateTime.now());
        assertFalse(AlerteClotureService.encorePertinente(direct));
        closed.setDateCloture(null);
        assertTrue(AlerteClotureService.encorePertinente(direct));
        var mentorAlert = new Alerte(); mentorAlert.setEncadreur(new Encadreur());
        assertTrue(AlerteClotureService.encorePertinente(mentorAlert));
    }

    @Test void startupExpiresOldAlertsWithoutDeletingHistoryOrChangingActiveDossiers() {
        var repository = mock(AlerteRepository.class);
        var closed = new StageParticipant(); closed.setDateCloture(LocalDateTime.now());
        var obsolete = new Alerte(); obsolete.setStageParticipant(closed); obsolete.setMessage("Rapport manquant");
        var active = new Alerte(); active.setStageParticipant(new StageParticipant());
        when(repository.findByStatutOrderByDateDetectionDesc(AlerteStatut.A_TRAITER)).thenReturn(List.of(obsolete, active));
        new AlerteClotureService(repository).expirerAlertesDejaCloturees();
        assertEquals(AlerteStatut.EXPIREE, obsolete.getStatut());
        assertNotNull(obsolete.getDateTraitement());
        assertNull(obsolete.getTraitePar());
        assertEquals("Rapport manquant", obsolete.getMessage());
        assertEquals(AlerteStatut.A_TRAITER, active.getStatut());
        verify(repository).save(obsolete);
        verify(repository, never()).save(active);
        verify(repository, never()).delete(any());
    }
}
