package mg.mef.stagemanagement;

import mg.mef.stagemanagement.dto.auth.AuthMeResponse;
import mg.mef.stagemanagement.model.*;
import mg.mef.stagemanagement.repository.*;
import mg.mef.stagemanagement.service.DossierAccessService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DossierAccessTests {
    @Mock ResponsableDepartementRepository departments;
    @Mock StageParticipantRepository participations;
    @Mock StagiaireRepository trainees;
    @Mock StageRepository stages;
    @Mock EncadreurRepository mentors;
    @Mock RapportActiviteRepository reports;
    @Mock EvaluationFinaleRepository evaluations;
    @Mock SoutenanceRepository defenses;
    @Mock StageParticipantFichierRepository participationFiles;
    @Mock StagiaireFichierRepository traineeFiles;
    @Mock StructureAccueilRepository structures;
    @Mock StagiaireAcademiqueRepository academics;
    @InjectMocks DossierAccessService access;
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    void login(int id, String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new AuthMeResponse(id, "test", List.of(role)), null, List.of()));
    }
    StageParticipant participation(int department, int mentorUser, int traineeUser) {
        var p = mock(StageParticipant.class, RETURNS_DEEP_STUBS);
        lenient().when(p.getStage().getStructureAccueil().getDepartement().getId()).thenReturn(department);
        lenient().when(p.getEncadreur().getUtilisateur().getId()).thenReturn(mentorUser);
        lenient().when(p.getStagiaireAcademique().getStagiaire().getUtilisateur().getId()).thenReturn(traineeUser);
        return p;
    }
    @Test void departmentManagersCannotReadOrWriteAnotherDepartment() {
        login(10, "RESPONSABLE_STAGE");
        when(departments.existsByUtilisateur_IdAndDepartement_Id(10, 1)).thenReturn(true);
        var own = participation(1, 20, 30); var other = participation(2, 21, 31);
        assertTrue(access.readParticipation(own)); assertTrue(access.manageParticipation(own));
        assertFalse(access.readParticipation(other)); assertFalse(access.manageParticipation(other));
        when(participations.findById(2)).thenReturn(Optional.of(other));
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> access.participation(2, true)).getStatusCode().value());
    }
    @Test void mentorsNeedActualAssignmentNotJustTheSameDepartment() {
        login(20, "ENCADREUR");
        assertTrue(access.readParticipation(participation(1, 20, 30)));
        assertFalse(access.readParticipation(participation(1, 21, 31)));
        assertFalse(access.manageParticipation(participation(1, 20, 30)));
    }
    @Test void traineesSeeOnlyTheirOwnDossiersAndCannotManageThem() {
        login(30, "STAGIAIRE");
        assertTrue(access.readParticipation(participation(1, 20, 30)));
        assertFalse(access.readParticipation(participation(1, 20, 31)));
        assertFalse(access.manageParticipation(participation(1, 20, 30)));
        login(31, "STAGIAIRE");
        assertFalse(access.readParticipation(participation(1, 20, 30)));
        assertTrue(access.readParticipation(participation(1, 20, 31)));
    }
    @Test void aFileWithAnUnauthorizedAssociationIsDenied() {
        login(30, "STAGIAIRE");
        var own = new StageParticipantFichier(); own.setStageParticipant(participation(1, 20, 30));
        var other = new StageParticipantFichier(); other.setStageParticipant(participation(2, 21, 31));
        when(participationFiles.findByFichier_Id(7)).thenReturn(List.of(own, other));
        assertThrows(ResponseStatusException.class, () -> access.file(7));
    }
    @Test void aMixedAlertCannotExposeAnOutsideDossier() {
        login(20, "ENCADREUR");
        var alert = new Alerte(); alert.setStageParticipant(participation(1, 20, 30));
        var report = new RapportActivite(); report.setStageParticipant(participation(2, 21, 31)); alert.setRapport(report);
        assertFalse(access.readAlert(alert));
    }
}
