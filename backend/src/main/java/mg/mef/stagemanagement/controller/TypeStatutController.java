package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.TypeStatut;
import mg.mef.stagemanagement.service.TypeStatutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/types-statut")
public class TypeStatutController {

    private final TypeStatutService service;

    public TypeStatutController(TypeStatutService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TypeStatut>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeStatut> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TypeStatut> create(@RequestBody TypeStatut typeStatut) {
        typeStatut.setId(null);
        TypeStatut saved = service.save(typeStatut);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeStatut> update(
            @PathVariable Integer id,
            @RequestBody TypeStatut typeStatut) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        typeStatut.setId(id);
        return ResponseEntity.ok(service.save(typeStatut));
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
