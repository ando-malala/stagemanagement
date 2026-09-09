package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.CritereEvaluation;
import mg.mef.stagemanagement.service.CritereEvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/criteres-evaluation")
public class CritereEvaluationController {

    private final CritereEvaluationService service;

    public CritereEvaluationController(CritereEvaluationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CritereEvaluation>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CritereEvaluation> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CritereEvaluation> create(@RequestBody CritereEvaluation critereEvaluation) {
        critereEvaluation.setId(null);
        CritereEvaluation saved = service.save(critereEvaluation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CritereEvaluation> update(
            @PathVariable Integer id,
            @RequestBody CritereEvaluation critereEvaluation) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        critereEvaluation.setId(id);
        return ResponseEntity.ok(service.save(critereEvaluation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
