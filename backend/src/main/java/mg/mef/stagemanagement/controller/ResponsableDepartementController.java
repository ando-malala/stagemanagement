package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.ResponsableDepartement;
import mg.mef.stagemanagement.service.ResponsableDepartementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responsable-departements")
public class ResponsableDepartementController {

    private final ResponsableDepartementService service;

    public ResponsableDepartementController(ResponsableDepartementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ResponsableDepartement>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsableDepartement> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ResponsableDepartement> create(@RequestBody ResponsableDepartement responsableDepartement) {
        responsableDepartement.setId(null);
        ResponsableDepartement saved = service.save(responsableDepartement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsableDepartement> update(
            @PathVariable Integer id,
            @RequestBody ResponsableDepartement responsableDepartement) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        responsableDepartement.setId(id);
        return ResponseEntity.ok(service.save(responsableDepartement));
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
