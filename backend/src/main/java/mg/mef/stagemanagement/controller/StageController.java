package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.Stage;
import mg.mef.stagemanagement.service.StageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
public class StageController {

    private final StageService service;

    public StageController(StageService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Stage>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stage> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Stage> create(@RequestBody Stage stage) {
        stage.setId(null);
        Stage saved = service.save(stage);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stage> update(
            @PathVariable Integer id,
            @RequestBody Stage stage) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stage.setId(id);
        return ResponseEntity.ok(service.save(stage));
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
