package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.StageParticipantFichier;
import mg.mef.stagemanagement.service.StageParticipantFichierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stage-participant-fichiers")
public class StageParticipantFichierController {

    private final StageParticipantFichierService service;

    public StageParticipantFichierController(StageParticipantFichierService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<StageParticipantFichier>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageParticipantFichier> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StageParticipantFichier> create(@RequestBody StageParticipantFichier stageParticipantFichier) {
        stageParticipantFichier.setId(null);
        StageParticipantFichier saved = service.save(stageParticipantFichier);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageParticipantFichier> update(
            @PathVariable Integer id,
            @RequestBody StageParticipantFichier stageParticipantFichier) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stageParticipantFichier.setId(id);
        return ResponseEntity.ok(service.save(stageParticipantFichier));
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
