package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.Stagiaire;
import mg.mef.stagemanagement.service.StagiaireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stagiaires")
public class StagiaireController {

    private final StagiaireService service;

    public StagiaireController(StagiaireService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Stagiaire>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stagiaire> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Stagiaire> create(@RequestBody Stagiaire stagiaire) {
        stagiaire.setId(null);
        Stagiaire saved = service.save(stagiaire);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stagiaire> update(
            @PathVariable Integer id,
            @RequestBody Stagiaire stagiaire) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stagiaire.setId(id);
        return ResponseEntity.ok(service.save(stagiaire));
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
