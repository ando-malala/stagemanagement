package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.StagiaireFichier;
import mg.mef.stagemanagement.service.StagiaireFichierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stagiaire-fichiers")
public class StagiaireFichierController {

    private final StagiaireFichierService service;

    public StagiaireFichierController(StagiaireFichierService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<StagiaireFichier>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StagiaireFichier> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StagiaireFichier> create(@RequestBody StagiaireFichier stagiaireFichier) {
        stagiaireFichier.setId(null);
        StagiaireFichier saved = service.save(stagiaireFichier);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StagiaireFichier> update(
            @PathVariable Integer id,
            @RequestBody StagiaireFichier stagiaireFichier) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stagiaireFichier.setId(id);
        return ResponseEntity.ok(service.save(stagiaireFichier));
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
