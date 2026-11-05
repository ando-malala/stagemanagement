package mg.mef.stagemanagement;

import mg.mef.stagemanagement.model.*;
import mg.mef.stagemanagement.repository.*;
import mg.mef.stagemanagement.service.*;
import mg.mef.stagemanagement.dto.stage.ChangerStatutParticipationRequest;
import mg.mef.stagemanagement.dto.stage.EvenementParticipationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClotureParticipationTests {
    @Mock DossierAccessService access;
    @Mock ParticipationRulesService rules;
    @Mock StageRepository stages;
    @Mock StageParticipantRepository participations;
    @Mock StructureAccueilRepository structures;
    @Mock StagiaireAcademiqueRepository academiques;
    @Mock CadreStageRepository cadres;
    @Mock EncadreurRepository encadreurs;
    @Mock StatutRepository statuts;
    @Mock StageParticipantStatutHistoriqueRepository historiques;
    @Mock StageParticipantEvenementRepository evenements;
    @Mock TypeEvenementParticipationRepository types;
    @Mock UtilisateurRepository utilisateurs;
    @Mock TransitionStatutRepository transitions;
    @Mock DocumentCatalogueService documents;
    @Mock AlerteClotureService alertesCloture;
    @InjectMocks StageGestionService service;
    StageParticipant participant;
    Statut cible;

    @BeforeEach void setup() {
        participant = mock(StageParticipant.class, Answers.RETURNS_DEEP_STUBS);
        when(participant.getId()).thenReturn(1);
        when(participant.getStatut().getId()).thenReturn(10);
        when(participant.getStatut().getCode()).thenReturn("A_CLOTURER");
        when(participant.getDateCloture()).thenReturn(null);
        when(participations.findForUpdate(1)).thenReturn(Optional.of(participant));
        when(participations.save(participant)).thenReturn(participant);
        when(utilisateurs.findById(2)).thenReturn(Optional.of(new Utilisateur()));
        cible = new Statut(); cible.setId(11); cible.setCode("CLOTUREE");
        when(statuts.findStatutByTypeCodeAndStatutCode("PARTICIPATION", "CLOTUREE")).thenReturn(Optional.of(cible));
        TypeEvenementParticipation type = new TypeEvenementParticipation(); type.setCode("CLOTURE");
        when(types.findByCode("CLOTURE")).thenReturn(Optional.of(type));
        when(transitions.existsByStatutSource_IdAndStatutCible_IdAndActifTrue(10, 11)).thenReturn(true);
    }
    @Test void clotureUsesCanonicalCodesAndWritesDateEventHistory() {
        service.cloturerParticipation(1, null, 2);
        verify(documents).verifierCloture(1);
        verify(participant).setStatut(cible);
        verify(participant).setDateCloture(any());
        verify(evenements).save(argThat(e -> "CLOTURE".equals(e.getTypeEvenement().getCode())));
        verify(historiques).save(argThat(h -> h.getStatut() == cible && h.getResponsable() != null));
        verify(types, never()).findByCode("CLOTUREE");
        verify(alertesCloture).expirerPourParticipation(1);
    }
    @Test void missingDocumentsPreventAllWrites() {
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT)).when(documents).verifierCloture(1);
        assertEquals(409, assertThrows(ResponseStatusException.class, () -> service.cloturerParticipation(1, null, 2)).getStatusCode().value());
        verifyNoInteractions(evenements, historiques);
        verify(participations, never()).save(any());
    }
    @Test void forbiddenTransitionPreventsClosure() {
        when(transitions.existsByStatutSource_IdAndStatutCible_IdAndActifTrue(10, 11)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> service.cloturerParticipation(1, null, 2));
        verifyNoInteractions(documents, evenements, historiques);
    }
    @Test void alreadyClosedIsRejected() {
        when(participant.getDateCloture()).thenReturn(java.time.LocalDateTime.now());
        assertThrows(ResponseStatusException.class, () -> service.cloturerParticipation(1, null, 2));
        verifyNoInteractions(documents, evenements, historiques);
    }
    @Test void statusPatchCannotBypassClosureRules() {
        ChangerStatutParticipationRequest request = new ChangerStatutParticipationRequest();
        request.setStatutCode("CLOTUREE");
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT)).when(documents).verifierCloture(1);
        assertThrows(ResponseStatusException.class, () -> service.changerStatutParticipation(1, request, 2));
        verify(documents).verifierCloture(1);
        verifyNoInteractions(evenements, historiques);
    }
    EvenementParticipationRequest overrideRequest() {
        var request = new EvenementParticipationRequest();
        request.setIgnorerDocumentsManquants(true);
        return request;
    }
    @Test void explicitOverrideClosesAndRecordsActualMissingDocuments() {
        var request = overrideRequest(); request.setMotif("Décision administrative");
        when(documents.obligatoires(1)).thenReturn(List.of(
                new DocumentCatalogueService.DocumentObligatoire(1, "CONVENTION_STAGE", "Convention", false),
                new DocumentCatalogueService.DocumentObligatoire(2, "RAPPORT_FINAL", "Rapport final", false),
                new DocumentCatalogueService.DocumentObligatoire(3, "AUTRE", "Pièce présente", true)));
        service.cloturerParticipation(1, request, 2);
        verify(access).participation(1, true);
        verify(rules).lock(participant);
        verify(documents, never()).verifierCloture(any());
        verify(rules).closureReports(participant);
        verify(participant).setDateCloture(any());
        verify(participant).setStatut(cible);
        String expected = "Décision administrative\nClôture malgré documents manquants : Convention, Rapport final";
        verify(evenements).save(argThat(e -> expected.equals(e.getMotif())));
        verify(historiques).save(argThat(h -> expected.equals(h.getMotif())));
        verify(alertesCloture).expirerPourParticipation(1);
    }
    @Test void falseOverrideStillRequiresDocuments() {
        var request = overrideRequest(); request.setIgnorerDocumentsManquants(false);
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT)).when(documents).verifierCloture(1);
        assertThrows(ResponseStatusException.class, () -> service.cloturerParticipation(1, request, 2));
        verifyNoInteractions(evenements, historiques);
        verify(participations, never()).save(any());
    }
    @Test void overrideDoesNotInventMissingDocumentsIfTheyWereUploadedMeanwhile() {
        var request = overrideRequest(); request.setMotif("Dossier complété");
        when(documents.obligatoires(1)).thenReturn(List.of(
                new DocumentCatalogueService.DocumentObligatoire(1, "CONVENTION_STAGE", "Convention", true)));
        service.cloturerParticipation(1, request, 2);
        verify(evenements).save(argThat(e -> "Dossier complété".equals(e.getMotif())));
    }
    @Test void overrideDoesNotBypassWeeklyReportValidation() {
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT)).when(rules).closureReports(participant);
        assertThrows(ResponseStatusException.class, () -> service.cloturerParticipation(1, overrideRequest(), 2));
        verifyNoInteractions(evenements, historiques);
        verify(participant, never()).setDateCloture(any());
        verify(participations, never()).save(any());
    }
    @Test void overrideDoesNotBypassAccessOrTransitions() {
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN)).when(access).participation(1, true);
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> service.cloturerParticipation(1, overrideRequest(), 2)).getStatusCode().value());
        verifyNoInteractions(documents, evenements, historiques);
        reset(access);
        when(transitions.existsByStatutSource_IdAndStatutCible_IdAndActifTrue(10, 11)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> service.cloturerParticipation(1, overrideRequest(), 2));
        verifyNoInteractions(documents, evenements, historiques);
    }
    @Test void forcedClosureBypassesDocumentsAndReportsAndAuditsBoth() {
        var request = new EvenementParticipationRequest();
        request.setForcerCloture(true);
        request.setMotif("Décision de l’administrateur");
        when(documents.obligatoires(1)).thenReturn(List.of(
                new DocumentCatalogueService.DocumentObligatoire(1, "CONVENTION_STAGE", "Convention", false)));
        var date = java.time.LocalDate.of(2026, 10, 5);
        when(rules.missingClosureReports(participant)).thenReturn(List.of(
                new CalculPeriodesRapportService.PeriodeRapport(date, date.plusDays(6), date.plusDays(7))));
        service.cloturerParticipation(1, request, 2);
        verify(access).participation(1, true);
        verify(documents, never()).verifierCloture(any());
        verify(rules, never()).closureReports(any());
        verify(participant).setDateCloture(any());
        verify(participant).setStatut(cible);
        String expected = "Décision de l’administrateur\nClôture forcée par décision administrative"
                + "\nClôture malgré documents manquants : Convention"
                + "\nRapports manquants ou non validés : du 2026-10-05 au 2026-10-11";
        verify(evenements).save(argThat(e -> expected.equals(e.getMotif())));
        verify(historiques).save(argThat(h -> expected.equals(h.getMotif())));
    }
    @Test void falseForceFlagStillRequiresValidatedReports() {
        var request = new EvenementParticipationRequest(); request.setForcerCloture(false);
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT)).when(rules).closureReports(participant);
        assertThrows(ResponseStatusException.class, () -> service.cloturerParticipation(1, request, 2));
        verifyNoInteractions(evenements, historiques);
        verify(participant, never()).setDateCloture(any());
    }
    @Test void forcedClosureStillRequiresAccessAndCannotCloseTwice() {
        var request = new EvenementParticipationRequest(); request.setForcerCloture(true);
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN)).when(access).participation(1, true);
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> service.cloturerParticipation(1, request, 2)).getStatusCode().value());
        reset(access);
        when(participant.getDateCloture()).thenReturn(java.time.LocalDateTime.now());
        assertThrows(ResponseStatusException.class, () -> service.cloturerParticipation(1, request, 2));
        verifyNoInteractions(documents, evenements, historiques);
        verify(participations, never()).save(any());
    }
}
