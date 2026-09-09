package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.StageParticipantStatutHistorique;
import mg.mef.stagemanagement.service.StageParticipantStatutHistoriqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stage-participant-statut-historique")
public class StageParticipantStatutHistoriqueController {

    private final StageParticipantStatutHistoriqueService service;

    public StageParticipantStatutHistoriqueController(StageParticipantStatutHistoriqueService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<StageParticipantStatutHistorique>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageParticipantStatutHistorique> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StageParticipantStatutHistorique> create(@RequestBody StageParticipantStatutHistorique stageParticipantStatutHistorique) {
        stageParticipantStatutHistorique.setId(null);
        StageParticipantStatutHistorique saved = service.save(stageParticipantStatutHistorique);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageParticipantStatutHistorique> update(
            @PathVariable Integer id,
            @RequestBody StageParticipantStatutHistorique stageParticipantStatutHistorique) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stageParticipantStatutHistorique.setId(id);
        return ResponseEntity.ok(service.save(stageParticipantStatutHistorique));
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
