package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.RapportActiviteStatutHistorique;
import mg.mef.stagemanagement.service.RapportActiviteStatutHistoriqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rapport-activite-statut-historique")
public class RapportActiviteStatutHistoriqueController {

    private final RapportActiviteStatutHistoriqueService service;

    public RapportActiviteStatutHistoriqueController(RapportActiviteStatutHistoriqueService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RapportActiviteStatutHistorique>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RapportActiviteStatutHistorique> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RapportActiviteStatutHistorique> create(@RequestBody RapportActiviteStatutHistorique rapportActiviteStatutHistorique) {
        rapportActiviteStatutHistorique.setId(null);
        RapportActiviteStatutHistorique saved = service.save(rapportActiviteStatutHistorique);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RapportActiviteStatutHistorique> update(
            @PathVariable Integer id,
            @RequestBody RapportActiviteStatutHistorique rapportActiviteStatutHistorique) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        rapportActiviteStatutHistorique.setId(id);
        return ResponseEntity.ok(service.save(rapportActiviteStatutHistorique));
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
