package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.CadreStage;
import mg.mef.stagemanagement.service.CadreStageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cadres-stage")
public class CadreStageController {

    private final CadreStageService service;

    public CadreStageController(CadreStageService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CadreStage>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CadreStage> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CadreStage> create(@RequestBody CadreStage cadreStage) {
        cadreStage.setId(null);
        CadreStage saved = service.save(cadreStage);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CadreStage> update(
            @PathVariable Integer id,
            @RequestBody CadreStage cadreStage) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        cadreStage.setId(id);
        return ResponseEntity.ok(service.save(cadreStage));
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
