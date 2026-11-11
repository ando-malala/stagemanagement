package mg.mef.stagemanagement;

import mg.mef.stagemanagement.model.*;
import mg.mef.stagemanagement.repository.*;
import mg.mef.stagemanagement.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompteEligibilityTests {
    final DossierAccessService access = mock(DossierAccessService.class);
    final StagiaireRepository trainees = mock(StagiaireRepository.class);
    final UtilisateurRepository users = mock(UtilisateurRepository.class);
    final UtilisateurRoleRepository roles = mock(UtilisateurRoleRepository.class);
    final PortailStagiaireService service = new PortailStagiaireService(access, trainees, users, roles);
    Stagiaire trainee; Utilisateur user;
    @BeforeEach void setup() {
        trainee = new Stagiaire(); trainee.setId(1);
        user = new Utilisateur(); user.setId(7); user.setIdentifiant("test-account");
        Statut status = new Statut(); status.setCode("ACTIF"); status.setActif(true); user.setStatut(status);
        when(access.role("ADMIN")).thenReturn(true);
        doAnswer(invocation -> { if (!invocation.getArgument(0, Boolean.class)) throw new ResponseStatusException(HttpStatus.FORBIDDEN); return null; }).when(access).require(anyBoolean());
        when(access.trainee(eq(1), anyBoolean())).thenReturn(trainee);
        when(users.findForUpdate(7)).thenReturn(Optional.of(user));
        when(roles.findByUtilisateur_Id(7)).thenReturn(List.of(role("STAGIAIRE", true)));
    }
    UtilisateurRole role(String code, boolean active) {
        Role role = new Role(); role.setCode(code); role.setActif(active);
        UtilisateurRole link = new UtilisateurRole(); link.setRole(role); link.setUtilisateur(user); return link;
    }
    void rejected(HttpStatus expected) {
        assertEquals(expected, assertThrows(ResponseStatusException.class, () -> service.associer(1, 7)).getStatusCode());
        verify(trainees, never()).saveAndFlush(any());
    }
    @Test void activeTraineeAccountAccepted() { assertEquals(7, service.associer(1, 7).utilisateurId()); verify(users).findForUpdate(7); }
    @Test void multipleRolesAccepted() { when(roles.findByUtilisateur_Id(7)).thenReturn(List.of(role("ADMIN", true), role("STAGIAIRE", true))); assertEquals(7, service.associer(1, 7).utilisateurId()); }
    @Test void forgedAdminOnlyAccountRejected() { when(roles.findByUtilisateur_Id(7)).thenReturn(List.of(role("ADMIN", true))); rejected(HttpStatus.CONFLICT); }
    @Test void inactiveAccountRejected() { user.getStatut().setCode("INACTIF"); rejected(HttpStatus.CONFLICT); }
    @Test void disabledStatusRejected() { user.getStatut().setActif(false); rejected(HttpStatus.CONFLICT); }
    @Test void inactiveRoleRejected() { when(roles.findByUtilisateur_Id(7)).thenReturn(List.of(role("STAGIAIRE", false))); rejected(HttpStatus.CONFLICT); }
    @Test void missingAccountRejected() { when(users.findForUpdate(7)).thenReturn(Optional.empty()); rejected(HttpStatus.NOT_FOUND); }
    @Test void alreadyUsedAccountRejected() { var other = new Stagiaire(); other.setId(2); when(trainees.findByUtilisateur_Id(7)).thenReturn(Optional.of(other)); rejected(HttpStatus.CONFLICT); }
    @Test void currentAccountRemainsEligible() { trainee.setUtilisateur(user); when(trainees.findByUtilisateur_Id(7)).thenReturn(Optional.of(trainee)); assertEquals(7, service.associer(1, 7).utilisateurId()); }
    @Test void unauthorizedCallerRejected() { when(access.role("ADMIN")).thenReturn(false); rejected(HttpStatus.FORBIDDEN); verifyNoInteractions(users); }
    @Test void eligibleListUsesCurrentTraineeAndMinimalProjection() { when(users.findEligibleTraineeAccounts(1)).thenReturn(List.of(user)); assertEquals(List.of(new PortailStagiaireService.Compte(7, "test-account")), service.comptesEligibles(1)); }
    @Test void explicitDissociationAccepted() { trainee.setUtilisateur(user); assertNull(service.associer(1, null).utilisateurId()); verifyNoInteractions(users); }
    @Test void emailValidationPreservesOptionalValuesAndRejectsMalformedInput() {
        assertNull(AssociationRules.email(null)); assertEquals("test@example.mg", AssociationRules.email("test@example.mg"));
        for (String invalid : List.of("abc", "a@@example.mg", "a b@example.mg")) assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class, () -> AssociationRules.email(invalid)).getStatusCode());
    }
}
