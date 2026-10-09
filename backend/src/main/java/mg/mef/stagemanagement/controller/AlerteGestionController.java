package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.alerte.AlerteRequest;
import mg.mef.stagemanagement.dto.alerte.AlerteResponse;
import mg.mef.stagemanagement.dto.auth.AuthMeResponse;
import mg.mef.stagemanagement.service.AlerteGestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertes")
public class AlerteGestionController {

    private final AlerteGestionService alerteGestionService;

    public AlerteGestionController(
            AlerteGestionService alerteGestionService
    ) {
        this.alerteGestionService = alerteGestionService;
    }

    @GetMapping
    public ResponseEntity<List<AlerteResponse>> listerAlertes() {
        return ResponseEntity.ok(
                alerteGestionService.listerAlertes()
        );
    }

    @GetMapping("/non-lues")
    public ResponseEntity<List<AlerteResponse>> listerAlertesATraiter() {
        return ResponseEntity.ok(
                alerteGestionService.listerAlertesATraiter()
        );
    }

    @PostMapping
    public ResponseEntity<AlerteResponse> creerAlerte(
            @RequestBody AlerteRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(alerteGestionService.creerAlerte(request));
    }

    @PatchMapping("/{id}/lire")
    public ResponseEntity<AlerteResponse> marquerCommeTraitee(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {
        return ResponseEntity.ok(
                alerteGestionService.marquerCommeTraitee(
                        id,
                        utilisateurConnecte.getUtilisateurId()
                )
        );
    }

    @PatchMapping("/tout-lire")
    public ResponseEntity<Void> toutMarquerCommeTraite(
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {
        alerteGestionService.toutMarquerCommeTraite(
                utilisateurConnecte.getUtilisateurId()
        );

        return ResponseEntity.noContent().build();
    }
}