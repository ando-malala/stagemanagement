package mg.mef.stagemanagement;

import mg.mef.stagemanagement.model.*;
import mg.mef.stagemanagement.repository.*;
import mg.mef.stagemanagement.service.*;
import mg.mef.stagemanagement.dto.stage.ChangerStatutParticipationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClotureParticipationTests {
    @Mock DossierAccessService access;
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
}
