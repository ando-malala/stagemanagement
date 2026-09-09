package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.DepartementStatutHistorique;
import mg.mef.stagemanagement.service.DepartementStatutHistoriqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departement-statut-historique")
public class DepartementStatutHistoriqueController {

    private final DepartementStatutHistoriqueService service;

    public DepartementStatutHistoriqueController(DepartementStatutHistoriqueService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DepartementStatutHistorique>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartementStatutHistorique> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DepartementStatutHistorique> create(@RequestBody DepartementStatutHistorique departementStatutHistorique) {
        departementStatutHistorique.setId(null);
        DepartementStatutHistorique saved = service.save(departementStatutHistorique);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartementStatutHistorique> update(
            @PathVariable Integer id,
            @RequestBody DepartementStatutHistorique departementStatutHistorique) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        departementStatutHistorique.setId(id);
        return ResponseEntity.ok(service.save(departementStatutHistorique));
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
