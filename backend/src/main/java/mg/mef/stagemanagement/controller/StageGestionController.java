package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.stage.StageParticipantRequest;
import mg.mef.stagemanagement.dto.stage.StageParticipantResponse;
import mg.mef.stagemanagement.dto.stage.StageRequest;
import mg.mef.stagemanagement.dto.stage.StageResponse;
import mg.mef.stagemanagement.service.StageGestionService;
import mg.mef.stagemanagement.dto.auth.AuthMeResponse;
import mg.mef.stagemanagement.dto.stage.ChangerStatutParticipationRequest;
import mg.mef.stagemanagement.dto.stage.ProlongerParticipationRequest;
import mg.mef.stagemanagement.dto.stage.EvenementParticipationRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StageGestionController {

    private final StageGestionService stageGestionService;

    public StageGestionController(StageGestionService stageGestionService) {
        this.stageGestionService = stageGestionService;
    }

    // =========================
    // STAGES
    // =========================

    @GetMapping("/stages")
    public ResponseEntity<List<StageResponse>> listerStages() {
        return ResponseEntity.ok(
                stageGestionService.listerStages()
        );
    }

    @GetMapping("/stages/{id}")
    public ResponseEntity<StageResponse> trouverStage(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                stageGestionService.trouverStage(id)
        );
    }

    @PostMapping("/stages")
    public ResponseEntity<StageResponse> creerStage(
            @RequestBody StageRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(stageGestionService.creerStage(request));
    }

    @PatchMapping("/stages/{id}")
    public ResponseEntity<StageResponse> modifierStage(
            @PathVariable Integer id,
            @RequestBody StageRequest request
    ) {
        return ResponseEntity.ok(
                stageGestionService.modifierStage(id, request)
        );
    }

    @GetMapping("/stages/{id}/participants")
    public ResponseEntity<List<StageParticipantResponse>> listerParticipantsStage(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                stageGestionService.listerParticipationsParStage(id)
        );
    }

    // =========================
    // PARTICIPATIONS
    // =========================

    @GetMapping("/stage-participants")
    public ResponseEntity<List<StageParticipantResponse>> listerParticipations() {
        return ResponseEntity.ok(
                stageGestionService.listerParticipations()
        );
    }

    @GetMapping("/stage-participants/{id}")
    public ResponseEntity<StageParticipantResponse> trouverParticipation(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                stageGestionService.trouverParticipation(id)
        );
    }

    @PostMapping("/stage-participants")
    public ResponseEntity<StageParticipantResponse> creerParticipation(
            @RequestBody StageParticipantRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(stageGestionService.creerParticipation(request));
    }

    @PatchMapping("/stage-participants/{id}")
    public ResponseEntity<StageParticipantResponse> modifierParticipation(
            @PathVariable Integer id,
            @RequestBody StageParticipantRequest request
    ) {
        return ResponseEntity.ok(
                stageGestionService.modifierParticipation(id, request)
        );
    }

    @PatchMapping("/stage-participants/{id}/statut")
    public ResponseEntity<StageParticipantResponse> changerStatutParticipation(
            @PathVariable Integer id,
            @RequestBody ChangerStatutParticipationRequest request,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {

        return ResponseEntity.ok(
                stageGestionService.changerStatutParticipation(
                        id,
                        request,
                        utilisateurConnecte.getUtilisateurId()
                )
        );
    }

    @PostMapping("/stage-participants/{id}/prolonger")
    public ResponseEntity<StageParticipantResponse> prolongerParticipation(
            @PathVariable Integer id,
            @RequestBody ProlongerParticipationRequest request,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {

        return ResponseEntity.ok(
                stageGestionService.prolongerParticipation(
                        id,
                        request,
                        utilisateurConnecte.getUtilisateurId()
                )
        );
    }

    @PostMapping("/stage-participants/{id}/suspendre")
    public ResponseEntity<StageParticipantResponse> suspendreParticipation(
            @PathVariable Integer id,
            @RequestBody EvenementParticipationRequest request,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {
        return ResponseEntity.ok(
                stageGestionService.suspendreParticipation(
                        id,
                        request,
                        utilisateurConnecte.getUtilisateurId()
                )
        );
    }

    @PostMapping("/stage-participants/{id}/reprendre")
    public ResponseEntity<StageParticipantResponse> reprendreParticipation(
            @PathVariable Integer id,
            @RequestBody EvenementParticipationRequest request,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {
        return ResponseEntity.ok(
                stageGestionService.reprendreParticipation(
                        id,
                        request,
                        utilisateurConnecte.getUtilisateurId()
                )
        );
    }

    @PostMapping("/stage-participants/{id}/abandonner")
    public ResponseEntity<StageParticipantResponse> abandonnerParticipation(
            @PathVariable Integer id,
            @RequestBody EvenementParticipationRequest request,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {
        return ResponseEntity.ok(
                stageGestionService.abandonnerParticipation(
                        id,
                        request,
                        utilisateurConnecte.getUtilisateurId()
                )
        );
    }

    @PostMapping("/stage-participants/{id}/annuler")
    public ResponseEntity<StageParticipantResponse> annulerParticipation(
            @PathVariable Integer id,
            @RequestBody EvenementParticipationRequest request,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {
        return ResponseEntity.ok(
                stageGestionService.annulerParticipation(
                        id,
                        request,
                        utilisateurConnecte.getUtilisateurId()
                )
        );
    }

    @PostMapping("/stage-participants/{id}/cloturer")
    public ResponseEntity<StageParticipantResponse> cloturerParticipation(
            @PathVariable Integer id,
            @RequestBody EvenementParticipationRequest request,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {
        return ResponseEntity.ok(
                stageGestionService.cloturerParticipation(
                        id,
                        request,
                        utilisateurConnecte.getUtilisateurId()
                )
        );
    }
}