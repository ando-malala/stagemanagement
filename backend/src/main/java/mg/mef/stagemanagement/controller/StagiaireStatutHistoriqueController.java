package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.StagiaireStatutHistorique;
import mg.mef.stagemanagement.service.StagiaireStatutHistoriqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stagiaire-statut-historique")
public class StagiaireStatutHistoriqueController {

    private final StagiaireStatutHistoriqueService service;

    public StagiaireStatutHistoriqueController(StagiaireStatutHistoriqueService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<StagiaireStatutHistorique>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StagiaireStatutHistorique> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StagiaireStatutHistorique> create(@RequestBody StagiaireStatutHistorique stagiaireStatutHistorique) {
        stagiaireStatutHistorique.setId(null);
        StagiaireStatutHistorique saved = service.save(stagiaireStatutHistorique);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StagiaireStatutHistorique> update(
            @PathVariable Integer id,
            @RequestBody StagiaireStatutHistorique stagiaireStatutHistorique) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stagiaireStatutHistorique.setId(id);
        return ResponseEntity.ok(service.save(stagiaireStatutHistorique));
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
