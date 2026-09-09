package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.Alerte;
import mg.mef.stagemanagement.service.AlerteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertes")
public class AlerteController {

    private final AlerteService service;

    public AlerteController(AlerteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Alerte>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alerte> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Alerte> create(@RequestBody Alerte alerte) {
        alerte.setId(null);
        Alerte saved = service.save(alerte);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alerte> update(
            @PathVariable Integer id,
            @RequestBody Alerte alerte) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        alerte.setId(id);
        return ResponseEntity.ok(service.save(alerte));
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
