package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.evaluation.CritereEvaluationResponse;
import mg.mef.stagemanagement.dto.evaluation.EvaluationCritereRequest;
import mg.mef.stagemanagement.dto.evaluation.EvaluationCritereResponse;
import mg.mef.stagemanagement.dto.evaluation.EvaluationFinaleRequest;
import mg.mef.stagemanagement.dto.evaluation.EvaluationFinaleResponse;
import mg.mef.stagemanagement.service.EvaluationGestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationGestionController {

    private final EvaluationGestionService service;

    public EvaluationGestionController(EvaluationGestionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EvaluationFinaleResponse>> listerEvaluations() {
        return ResponseEntity.ok(service.listerEvaluations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluationFinaleResponse> trouverEvaluation(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.trouverEvaluation(id));
    }

    @GetMapping("/participation/{stageParticipantId}")
    public ResponseEntity<EvaluationFinaleResponse> trouverParParticipation(
            @PathVariable Integer stageParticipantId
    ) {
        return ResponseEntity.ok(
                service.trouverEvaluationParParticipation(stageParticipantId)
        );
    }

    @PostMapping
    public ResponseEntity<EvaluationFinaleResponse> creerEvaluation(
            @RequestBody EvaluationFinaleRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.creerEvaluation(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EvaluationFinaleResponse> modifierEvaluation(
            @PathVariable Integer id,
            @RequestBody EvaluationFinaleRequest request
    ) {
        return ResponseEntity.ok(
                service.modifierEvaluation(id, request)
        );
    }

    @GetMapping("/criteres")
    public ResponseEntity<List<CritereEvaluationResponse>> listerCriteres() {
        return ResponseEntity.ok(service.listerCriteresActifs());
    }

    @GetMapping("/{id}/notes")
    public ResponseEntity<List<EvaluationCritereResponse>> listerNotes(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.listerNotes(id));
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<EvaluationCritereResponse> noterCritere(
            @PathVariable Integer id,
            @RequestBody EvaluationCritereRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.noterCritere(id, request));
    }

    @PostMapping("/{id}/finaliser")
    public ResponseEntity<EvaluationFinaleResponse> finaliserEvaluation(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.finaliserEvaluation(id));
    }
}