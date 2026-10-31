package mg.mef.stagemanagement;

import mg.mef.stagemanagement.model.*;
import mg.mef.stagemanagement.repository.*;
import mg.mef.stagemanagement.service.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SuiviServicesTests {
    @Test void requiredDocumentsRespectConfigurationAndPresence() {
        var types = mock(TypePieceJointeRepository.class);
        var files = mock(StageParticipantFichierRepository.class);
        var participants = mock(StageParticipantRepository.class);
        when(participants.existsById(1)).thenReturn(true);
        var required = new TypePieceJointe(); required.setId(4); required.setCode("CONVENTION_STAGE"); required.setLibelle("Convention");
        when(types.findByPourParticipationTrueAndObligatoireClotureTrueAndActifTrue()).thenReturn(List.of(required));
        when(files.findByStageParticipant_Id(1)).thenReturn(List.of());
        var service = new DocumentCatalogueService(types, files, participants);
        assertFalse(service.obligatoires(1).get(0).present());
        assertThrows(ResponseStatusException.class, () -> service.verifierCloture(1));
        var link = new StageParticipantFichier(); link.setTypePieceJointe(required);
        when(files.findByStageParticipant_Id(1)).thenReturn(List.of(link));
        assertDoesNotThrow(() -> service.verifierCloture(1));
        assertThrows(ResponseStatusException.class, () -> service.obligatoires(99));
    }
    @Test void periodsUseMondayDeadlineAndSubmissionDate() {
        var service = new SuiviParticipationService(null, null, null, null, null, new CalculPeriodesRapportService(), null);
        var p = new StageParticipant(); p.setDateDebutStage(LocalDate.of(2026, 10, 1)); p.setDateFinStage(LocalDate.of(2026, 10, 4));
        var status = new Statut(); status.setCode("EN_COURS"); p.setStatut(status);
        assertFalse(service.periodes(p, List.of(), LocalDate.of(2026, 10, 5)).get(0).manquante());
        assertTrue(service.periodes(p, List.of(), LocalDate.of(2026, 10, 6)).get(0).manquante());
        var rapport = new RapportActivite(); rapport.setDebutSemaine(p.getDateDebutStage()); rapport.setFinSemaine(p.getDateFinStage());
        var reportStatus = new Statut(); reportStatus.setCode("VALIDE"); rapport.setStatut(reportStatus);
        assertTrue(service.periodes(p, List.of(rapport), LocalDate.of(2026, 10, 6)).get(0).manquante());
        rapport.setDateSoumission(LocalDateTime.of(2026, 10, 5, 12, 0));
        var valid = service.periodes(p, List.of(rapport), LocalDate.of(2026, 10, 6)).get(0);
        assertTrue(valid.validee()); assertTrue(valid.soumise()); assertFalse(valid.manquante());
        reportStatus.setCode("A_CORRIGER");
        assertFalse(service.periodes(p, List.of(rapport), LocalDate.of(2026, 10, 6)).get(0).manquante());
        status.setCode("ANNULEE");
        assertFalse(service.periodes(p, List.of(), LocalDate.of(2026, 10, 6)).get(0).exigible());
    }
}
