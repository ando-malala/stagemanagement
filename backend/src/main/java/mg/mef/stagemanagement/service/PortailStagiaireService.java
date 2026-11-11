package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PortailStagiaireService {
    public record Compte(Integer utilisateurId, String identifiant) {}
    public record Profil(Integer stagiaireId) {}
    private final DossierAccessService access;
    private final StagiaireRepository trainees;
    private final UtilisateurRepository users;
    private final UtilisateurRoleRepository roles;
    public PortailStagiaireService(DossierAccessService access, StagiaireRepository trainees,
            UtilisateurRepository users, UtilisateurRoleRepository roles) {
        this.access = access; this.trainees = trainees; this.users = users; this.roles = roles;
    }
    public Profil me() {
        access.require(access.role("STAGIAIRE"));
        return new Profil(trainees.findByUtilisateur_Id(access.user().getUtilisateurId())
                .map(t -> t.getId()).orElse(null));
    }
    public Compte compte(Integer id) {
        access.require(access.role("ADMIN"));
        var t = access.trainee(id, false);
        return new Compte(t.getUtilisateur() == null ? null : t.getUtilisateur().getId(),
                t.getUtilisateur() == null ? null : t.getUtilisateur().getIdentifiant());
    }
    public java.util.List<Compte> comptesEligibles(Integer id) {
        access.require(access.role("ADMIN"));
        access.trainee(id, false);
        return users.findEligibleTraineeAccounts(id).stream()
                .map(u -> new Compte(u.getId(), u.getIdentifiant())).toList();
    }
    @Transactional
    public Compte associer(Integer id, Integer userId) {
        access.require(access.role("ADMIN"));
        var t = access.trainee(id, true);
        if (userId == null) t.setUtilisateur(null);
        else {
            var u = users.findForUpdate(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compte introuvable"));
            boolean traineeRole = roles.findByUtilisateur_Id(userId).stream()
                    .anyMatch(r -> "STAGIAIRE".equals(r.getRole().getCode()) && Boolean.TRUE.equals(r.getRole().getActif()));
            if (!traineeRole || u.getStatut() == null || !"ACTIF".equals(u.getStatut().getCode()) || !Boolean.TRUE.equals(u.getStatut().getActif()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Un compte stagiaire actif est requis");
            trainees.findByUtilisateur_Id(userId).filter(other -> !other.getId().equals(id)).ifPresent(other -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce compte est deja associe a un autre stagiaire");
            });
            t.setUtilisateur(u);
        }
        trainees.saveAndFlush(t); // Existing UNIQUE(utilisateur_id) also protects concurrent association.
        return compte(id);
    }
}
