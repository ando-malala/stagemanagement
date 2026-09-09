package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.EvaluationFinale;
import mg.mef.stagemanagement.service.EvaluationFinaleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations-finales")
public class EvaluationFinaleController {

    private final EvaluationFinaleService service;

    public EvaluationFinaleController(EvaluationFinaleService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EvaluationFinale>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluationFinale> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EvaluationFinale> create(@RequestBody EvaluationFinale evaluationFinale) {
        evaluationFinale.setId(null);
        EvaluationFinale saved = service.save(evaluationFinale);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvaluationFinale> update(
            @PathVariable Integer id,
            @RequestBody EvaluationFinale evaluationFinale) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        evaluationFinale.setId(id);
        return ResponseEntity.ok(service.save(evaluationFinale));
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
