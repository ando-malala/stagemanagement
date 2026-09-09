package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.Encadreur;
import mg.mef.stagemanagement.service.EncadreurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encadreurs")
public class EncadreurController {

    private final EncadreurService service;

    public EncadreurController(EncadreurService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Encadreur>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Encadreur> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Encadreur> create(@RequestBody Encadreur encadreur) {
        encadreur.setId(null);
        Encadreur saved = service.save(encadreur);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Encadreur> update(
            @PathVariable Integer id,
            @RequestBody Encadreur encadreur) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        encadreur.setId(id);
        return ResponseEntity.ok(service.save(encadreur));
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
