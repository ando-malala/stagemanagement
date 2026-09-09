package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.Statut;
import mg.mef.stagemanagement.service.StatutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuts")
public class StatutController {

    private final StatutService service;

    public StatutController(StatutService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Statut>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Statut> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Statut> create(@RequestBody Statut statut) {
        statut.setId(null);
        Statut saved = service.save(statut);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Statut> update(
            @PathVariable Integer id,
            @RequestBody Statut statut) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        statut.setId(id);
        return ResponseEntity.ok(service.save(statut));
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
