package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.StageParticipantEvenement;
import mg.mef.stagemanagement.service.StageParticipantEvenementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stage-participant-evenements")
public class StageParticipantEvenementController {

    private final StageParticipantEvenementService service;

    public StageParticipantEvenementController(StageParticipantEvenementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<StageParticipantEvenement>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageParticipantEvenement> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StageParticipantEvenement> create(@RequestBody StageParticipantEvenement stageParticipantEvenement) {
        stageParticipantEvenement.setId(null);
        StageParticipantEvenement saved = service.save(stageParticipantEvenement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageParticipantEvenement> update(
            @PathVariable Integer id,
            @RequestBody StageParticipantEvenement stageParticipantEvenement) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stageParticipantEvenement.setId(id);
        return ResponseEntity.ok(service.save(stageParticipantEvenement));
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
