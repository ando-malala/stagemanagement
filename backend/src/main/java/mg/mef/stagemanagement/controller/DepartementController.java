package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.Departement;
import mg.mef.stagemanagement.service.DepartementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departements")
public class DepartementController {

    private final DepartementService service;

    public DepartementController(DepartementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Departement>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departement> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Departement> create(@RequestBody Departement departement) {
        departement.setId(null);
        Departement saved = service.save(departement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Departement> update(
            @PathVariable Integer id,
            @RequestBody Departement departement) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        departement.setId(id);
        return ResponseEntity.ok(service.save(departement));
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
