package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.auth.AuthMeResponse;
import mg.mef.stagemanagement.dto.rapport.ChangerStatutRapportRequest;
import mg.mef.stagemanagement.dto.rapport.RapportActiviteRequest;
import mg.mef.stagemanagement.dto.rapport.RapportActiviteResponse;
import mg.mef.stagemanagement.dto.rapport.RapportObservationRequest;
import mg.mef.stagemanagement.dto.rapport.RapportObservationResponse;
import mg.mef.stagemanagement.service.RapportActiviteGestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rapports-activite")
public class RapportActiviteGestionController {

    private final RapportActiviteGestionService service;

    public RapportActiviteGestionController(
            RapportActiviteGestionService service
    ) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RapportActiviteResponse>> listerRapports() {
        return ResponseEntity.ok(service.listerRapports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RapportActiviteResponse> trouverRapport(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.trouverRapport(id));
    }

    @GetMapping("/participation/{stageParticipantId}")
    public ResponseEntity<List<RapportActiviteResponse>> listerParParticipation(
            @PathVariable Integer stageParticipantId
    ) {
        return ResponseEntity.ok(
                service.listerRapportsParParticipation(stageParticipantId)
        );
    }

    @PostMapping
    public ResponseEntity<RapportActiviteResponse> creerRapport(
            @RequestBody RapportActiviteRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.creerRapport(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RapportActiviteResponse> modifierRapport(
            @PathVariable Integer id,
            @RequestBody RapportActiviteRequest request
    ) {
        return ResponseEntity.ok(
                service.modifierRapport(id, request)
        );
    }

    @GetMapping("/{id}/observations")
    public ResponseEntity<List<RapportObservationResponse>> listerObservations(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                service.listerObservations(id)
        );
    }

    @PostMapping("/{id}/observations")
    public ResponseEntity<RapportObservationResponse> ajouterObservation(
            @PathVariable Integer id,
            @RequestBody RapportObservationRequest request,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.ajouterObservation(
                                id,
                                request,
                                utilisateurConnecte.getUtilisateurId()
                        )
                );
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<RapportActiviteResponse> changerStatut(
            @PathVariable Integer id,
            @RequestBody ChangerStatutRapportRequest request,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {
        return ResponseEntity.ok(
                service.changerStatutRapport(
                        id,
                        request,
                        utilisateurConnecte.getUtilisateurId()
                )
        );
    }
}