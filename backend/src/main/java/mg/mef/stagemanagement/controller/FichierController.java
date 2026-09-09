package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.Fichier;
import mg.mef.stagemanagement.service.FichierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fichiers")
public class FichierController {

    private final FichierService service;

    public FichierController(FichierService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Fichier>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fichier> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Fichier> create(@RequestBody Fichier fichier) {
        fichier.setId(null);
        Fichier saved = service.save(fichier);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fichier> update(
            @PathVariable Integer id,
            @RequestBody Fichier fichier) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        fichier.setId(id);
        return ResponseEntity.ok(service.save(fichier));
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
