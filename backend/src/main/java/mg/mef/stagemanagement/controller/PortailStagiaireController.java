package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.service.PortailStagiaireService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PortailStagiaireController {
    public record CompteRequest(Integer utilisateurId) {}
    private final PortailStagiaireService portail;
    public PortailStagiaireController(PortailStagiaireService portail) { this.portail = portail; }
    @GetMapping("/stagiaires/{id}/compte")
    public PortailStagiaireService.Compte compte(@PathVariable Integer id) { return portail.compte(id); }
    @PatchMapping("/stagiaires/{id}/compte")
    public PortailStagiaireService.Compte associer(@PathVariable Integer id, @RequestBody CompteRequest request) {
        return portail.associer(id, request.utilisateurId());
    }
    @GetMapping("/portail/me")
    public PortailStagiaireService.Profil me() { return portail.me(); }
}
