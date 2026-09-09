package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.Filiere;
import mg.mef.stagemanagement.service.FiliereService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filieres")
public class FiliereController {

    private final FiliereService service;

    public FiliereController(FiliereService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Filiere>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Filiere> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Filiere> create(@RequestBody Filiere filiere) {
        filiere.setId(null);
        Filiere saved = service.save(filiere);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Filiere> update(
            @PathVariable Integer id,
            @RequestBody Filiere filiere) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        filiere.setId(id);
        return ResponseEntity.ok(service.save(filiere));
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
