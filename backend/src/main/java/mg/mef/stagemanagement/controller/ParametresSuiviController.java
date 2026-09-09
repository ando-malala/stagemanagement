package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.ParametresSuivi;
import mg.mef.stagemanagement.service.ParametresSuiviService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parametres-suivi")
public class ParametresSuiviController {

    private final ParametresSuiviService service;

    public ParametresSuiviController(ParametresSuiviService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ParametresSuivi>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParametresSuivi> findById(@PathVariable Short id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ParametresSuivi> create(@RequestBody ParametresSuivi parametresSuivi) {
        parametresSuivi.setId(null);
        ParametresSuivi saved = service.save(parametresSuivi);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParametresSuivi> update(
            @PathVariable Short id,
            @RequestBody ParametresSuivi parametresSuivi) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        parametresSuivi.setId(id);
        return ResponseEntity.ok(service.save(parametresSuivi));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Short id) {
        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
