package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.auth.AuthMeResponse;
import mg.mef.stagemanagement.dto.encadreur.ChangerStatutEncadreurRequest;
import mg.mef.stagemanagement.dto.encadreur.EncadreurRequest;
import mg.mef.stagemanagement.dto.encadreur.EncadreurResponse;
import mg.mef.stagemanagement.service.EncadreurGestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encadreurs")
public class EncadreurGestionController {
        public static class AssocierCompteRequest {
                private Integer utilisateurId;

                public Integer getUtilisateurId() {
                        return utilisateurId;
                }

                public void setUtilisateurId(Integer utilisateurId) {
                        this.utilisateurId = utilisateurId;
                }
        }

    @GetMapping("/comptes-eligibles")
    public java.util.List<mg.mef.stagemanagement.service.PortailStagiaireService.Compte> comptesEligibles(
            @RequestParam(required = false) Integer encadreurId) {
        return encadreurGestionService.comptesEligibles(encadreurId);
    }

        @GetMapping("/{id}/compte")
        public mg.mef.stagemanagement.service.PortailStagiaireService.Compte compte(@PathVariable Integer id) {
                return encadreurGestionService.compte(id);
        }

        @PatchMapping("/{id}/compte")
        public mg.mef.stagemanagement.service.PortailStagiaireService.Compte associerCompte(
                        @PathVariable Integer id,
                        @RequestBody AssocierCompteRequest request
        ) {
                return encadreurGestionService.associerCompte(
                                id,
                                request == null ? null : request.getUtilisateurId()
                );
        }

    private final EncadreurGestionService encadreurGestionService;

    public EncadreurGestionController(
            EncadreurGestionService encadreurGestionService
    ) {
        this.encadreurGestionService = encadreurGestionService;
    }

    @GetMapping
    public ResponseEntity<List<EncadreurResponse>> listerEncadreurs() {
        return ResponseEntity.ok(
                encadreurGestionService.listerEncadreurs()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncadreurResponse> trouverEncadreur(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                encadreurGestionService.trouverEncadreur(id)
        );
    }

    @PostMapping
    public ResponseEntity<EncadreurResponse> creerEncadreur(
            @RequestBody EncadreurRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(encadreurGestionService.creerEncadreur(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EncadreurResponse> modifierEncadreur(
            @PathVariable Integer id,
            @RequestBody EncadreurRequest request
    ) {
        return ResponseEntity.ok(
                encadreurGestionService.modifierEncadreur(id, request)
        );
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<EncadreurResponse> changerStatut(
            @PathVariable Integer id,
            @RequestBody ChangerStatutEncadreurRequest request,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {
        return ResponseEntity.ok(
                encadreurGestionService.changerStatut(
                        id,
                        request,
                        utilisateurConnecte.getUtilisateurId()
                )
        );
    }
}
