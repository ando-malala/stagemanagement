package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.MoisStage;
import mg.mef.stagemanagement.service.MoisStageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mois-stage")
public class MoisStageController {

    private final MoisStageService service;

    public MoisStageController(MoisStageService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MoisStage>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoisStage> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MoisStage> create(@RequestBody MoisStage moisStage) {
        moisStage.setId(null);
        MoisStage saved = service.save(moisStage);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MoisStage> update(
            @PathVariable Integer id,
            @RequestBody MoisStage moisStage) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        moisStage.setId(id);
        return ResponseEntity.ok(service.save(moisStage));
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
