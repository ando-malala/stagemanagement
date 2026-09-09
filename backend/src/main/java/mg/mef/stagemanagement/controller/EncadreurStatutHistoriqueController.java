package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.EncadreurStatutHistorique;
import mg.mef.stagemanagement.service.EncadreurStatutHistoriqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encadreur-statut-historique")
public class EncadreurStatutHistoriqueController {

    private final EncadreurStatutHistoriqueService service;

    public EncadreurStatutHistoriqueController(EncadreurStatutHistoriqueService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EncadreurStatutHistorique>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncadreurStatutHistorique> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EncadreurStatutHistorique> create(@RequestBody EncadreurStatutHistorique encadreurStatutHistorique) {
        encadreurStatutHistorique.setId(null);
        EncadreurStatutHistorique saved = service.save(encadreurStatutHistorique);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EncadreurStatutHistorique> update(
            @PathVariable Integer id,
            @RequestBody EncadreurStatutHistorique encadreurStatutHistorique) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        encadreurStatutHistorique.setId(id);
        return ResponseEntity.ok(service.save(encadreurStatutHistorique));
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
