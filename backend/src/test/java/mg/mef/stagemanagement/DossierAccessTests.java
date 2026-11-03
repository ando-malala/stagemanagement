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
    void login(int id, String... roles) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new AuthMeResponse(id, "test", List.of(roles)), null, List.of()));
    }
    StageParticipant participation(int department, int mentorUser, int traineeUser) {
        var p = mock(StageParticipant.class, RETURNS_DEEP_STUBS);
        lenient().when(p.getStage().getStructureAccueil().getDepartement().getId()).thenReturn(department);
        lenient().when(p.getEncadreur().getUtilisateur().getId()).thenReturn(mentorUser);
        lenient().when(p.getStagiaireAcademique().getStagiaire().getUtilisateur().getId()).thenReturn(traineeUser);
        return p;
    }
    @Test void reportWorkflowBelongsToTheTraineeAndAssignedMentorEvenWithMultipleRoles() {
        var p = participation(1, 20, 30);
        when(p.getId()).thenReturn(7);
        var report = new RapportActivite(); report.setStageParticipant(p);
        var status = new Statut(); status.setCode("BROUILLON"); report.setStatut(status);
        when(reports.findById(5)).thenReturn(Optional.of(report));
        login(30, "STAGIAIRE", "ADMIN");
        assertTrue(access.canAuthorReport(p)); assertFalse(access.canReviewReport(p));
        access.reportContent(5, 7); access.reportTransition(5, "SOUMIS");
        assertThrows(ResponseStatusException.class, () -> access.reportContent(5, 8));
        assertThrows(ResponseStatusException.class, () -> access.reportTransition(5, "VALIDE"));
        login(10, "ADMIN");
        assertFalse(access.canAuthorReport(p)); assertFalse(access.canReviewReport(p));
        assertThrows(ResponseStatusException.class, () -> access.reportContent(5, 7));
        assertThrows(ResponseStatusException.class, () -> access.reportTransition(5, "SOUMIS"));
        status.setCode("SOUMIS");
        assertThrows(ResponseStatusException.class, () -> access.reportTransition(5, "VALIDE"));
        login(20, "ENCADREUR", "ADMIN");
        assertTrue(access.canReviewReport(p)); assertFalse(access.canAuthorReport(p));
        access.reportTransition(5, "VALIDE"); access.reportTransition(5, "A_CORRIGER");
        assertThrows(ResponseStatusException.class, () -> access.reportContent(5, 7));
        assertThrows(ResponseStatusException.class, () -> access.reportTransition(5, "SOUMIS"));
        login(21, "ENCADREUR", "ADMIN");
        assertThrows(ResponseStatusException.class, () -> access.reportTransition(5, "VALIDE"));
        login(31, "STAGIAIRE", "ADMIN");
        assertThrows(ResponseStatusException.class, () -> access.reportContent(5, 7));
        login(30, "STAGIAIRE", "ENCADREUR", "ADMIN");
        assertFalse(access.canReviewReport(participation(1, 30, 30)));
        assertThrows(ResponseStatusException.class, () -> access.reportContent(5, 7));
        status.setCode("A_CORRIGER");
        access.reportContent(5, 7); access.reportTransition(5, "SOUMIS");
        status.setCode("VALIDE");
        assertThrows(ResponseStatusException.class, () -> access.reportContent(5, 7));
        assertThrows(ResponseStatusException.class, () -> access.reportTransition(5, "SOUMIS"));
    }
    @Test void administratorsManageParticipationsAcrossDepartments() {
        login(10, "ADMIN");
        var own = participation(1, 20, 30); var other = participation(2, 21, 31);
        assertTrue(access.readParticipation(own)); assertTrue(access.manageParticipation(own));
        assertTrue(access.readParticipation(other)); assertTrue(access.manageParticipation(other));
        when(participations.findById(2)).thenReturn(Optional.of(other));
        assertSame(other, access.participation(2, true));
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
    @Test void administratorsReadGlobalWorkloadAndAssignedMentorsReadTheirOwn() {
        login(10, "ADMIN");
        var department = new Departement(); department.setId(1);
        var mentor = new Encadreur(); mentor.setId(7); mentor.setDepartement(department);
        var type = new TypeAlerte(); type.setCode("ENCADREUR_SURCHARGE");
        var alert = new Alerte(); alert.setTypeAlerte(type); alert.setEncadreur(mentor);
        assertTrue(access.readAlert(alert));
        login(20, "ENCADREUR"); var user = new Utilisateur(); user.setId(20); mentor.setUtilisateur(user);
        assertTrue(access.readAlert(alert));
    }

    @Test void unassignedTraineeNeedsAdminOrItsOwnAccount() {
        var trainee = new Stagiaire(); trainee.setId(42);
        var owner = new Utilisateur(); owner.setId(30); trainee.setUtilisateur(owner);
        when(trainees.findById(42)).thenReturn(Optional.of(trainee));
        login(10, "ADMIN"); assertSame(trainee, access.trainee(42, true));
        login(20, "ENCADREUR"); assertFalse(access.readTrainee(trainee));
        login(31, "STAGIAIRE"); assertFalse(access.readTrainee(trainee));
        login(30, "STAGIAIRE"); assertTrue(access.readTrainee(trainee));
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> access.trainee(42, true)).getStatusCode().value());
    }

    @Test void mentorReadsTraineeViaActualParticipationWithoutOwningOtherParticipations() {
        var trainee = new Stagiaire(); trainee.setId(42);
        var own = participation(1, 20, 30); var other = participation(2, 21, 30);
        when(participations.findByStagiaireAcademique_Stagiaire_Id(42)).thenReturn(List.of(own, other));
        login(20, "ENCADREUR");
        assertTrue(access.readTrainee(trainee));
        assertFalse(access.readParticipation(other));
        login(22, "ENCADREUR"); assertFalse(access.readTrainee(trainee));
    }
}
