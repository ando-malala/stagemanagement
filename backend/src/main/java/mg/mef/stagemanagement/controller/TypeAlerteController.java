package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.TypeAlerte;
import mg.mef.stagemanagement.service.TypeAlerteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/types-alerte")
public class TypeAlerteController {

    private final TypeAlerteService service;

    public TypeAlerteController(TypeAlerteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TypeAlerte>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeAlerte> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TypeAlerte> create(@RequestBody TypeAlerte typeAlerte) {
        typeAlerte.setId(null);
        TypeAlerte saved = service.save(typeAlerte);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeAlerte> update(
            @PathVariable Integer id,
            @RequestBody TypeAlerte typeAlerte) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        typeAlerte.setId(id);
        return ResponseEntity.ok(service.save(typeAlerte));
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
