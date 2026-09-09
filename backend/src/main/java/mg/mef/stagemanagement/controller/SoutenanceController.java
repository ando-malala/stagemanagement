package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.Soutenance;
import mg.mef.stagemanagement.service.SoutenanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/soutenances")
public class SoutenanceController {

    private final SoutenanceService service;

    public SoutenanceController(SoutenanceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Soutenance>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Soutenance> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Soutenance> create(@RequestBody Soutenance soutenance) {
        soutenance.setId(null);
        Soutenance saved = service.save(soutenance);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Soutenance> update(
            @PathVariable Integer id,
            @RequestBody Soutenance soutenance) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        soutenance.setId(id);
        return ResponseEntity.ok(service.save(soutenance));
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
