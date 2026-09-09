package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.EvaluationCritere;
import mg.mef.stagemanagement.service.EvaluationCritereService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation-criteres")
public class EvaluationCritereController {

    private final EvaluationCritereService service;

    public EvaluationCritereController(EvaluationCritereService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EvaluationCritere>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluationCritere> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EvaluationCritere> create(@RequestBody EvaluationCritere evaluationCritere) {
        evaluationCritere.setId(null);
        EvaluationCritere saved = service.save(evaluationCritere);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvaluationCritere> update(
            @PathVariable Integer id,
            @RequestBody EvaluationCritere evaluationCritere) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        evaluationCritere.setId(id);
        return ResponseEntity.ok(service.save(evaluationCritere));
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
