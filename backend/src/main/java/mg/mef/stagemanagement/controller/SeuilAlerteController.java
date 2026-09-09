package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.SeuilAlerte;
import mg.mef.stagemanagement.service.SeuilAlerteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seuils-alerte")
public class SeuilAlerteController {

    private final SeuilAlerteService service;

    public SeuilAlerteController(SeuilAlerteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SeuilAlerte>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeuilAlerte> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SeuilAlerte> create(@RequestBody SeuilAlerte seuilAlerte) {
        seuilAlerte.setId(null);
        SeuilAlerte saved = service.save(seuilAlerte);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeuilAlerte> update(
            @PathVariable Integer id,
            @RequestBody SeuilAlerte seuilAlerte) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        seuilAlerte.setId(id);
        return ResponseEntity.ok(service.save(seuilAlerte));
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
