package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.StageParticipant;
import mg.mef.stagemanagement.service.StageParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stage-participants")
public class StageParticipantController {

    private final StageParticipantService service;

    public StageParticipantController(StageParticipantService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<StageParticipant>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageParticipant> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StageParticipant> create(@RequestBody StageParticipant stageParticipant) {
        stageParticipant.setId(null);
        StageParticipant saved = service.save(stageParticipant);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageParticipant> update(
            @PathVariable Integer id,
            @RequestBody StageParticipant stageParticipant) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stageParticipant.setId(id);
        return ResponseEntity.ok(service.save(stageParticipant));
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
