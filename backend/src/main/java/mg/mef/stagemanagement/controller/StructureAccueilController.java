package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.StructureAccueil;
import mg.mef.stagemanagement.service.StructureAccueilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/structures-accueil")
public class StructureAccueilController {

    private final StructureAccueilService service;

    public StructureAccueilController(StructureAccueilService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<StructureAccueil>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StructureAccueil> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StructureAccueil> create(@RequestBody StructureAccueil structureAccueil) {
        structureAccueil.setId(null);
        StructureAccueil saved = service.save(structureAccueil);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StructureAccueil> update(
            @PathVariable Integer id,
            @RequestBody StructureAccueil structureAccueil) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        structureAccueil.setId(id);
        return ResponseEntity.ok(service.save(structureAccueil));
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
