package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.RapportActivite;
import mg.mef.stagemanagement.service.RapportActiviteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rapports-activite")
public class RapportActiviteController {

    private final RapportActiviteService service;

    public RapportActiviteController(RapportActiviteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RapportActivite>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RapportActivite> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RapportActivite> create(@RequestBody RapportActivite rapportActivite) {
        rapportActivite.setId(null);
        RapportActivite saved = service.save(rapportActivite);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RapportActivite> update(
            @PathVariable Integer id,
            @RequestBody RapportActivite rapportActivite) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        rapportActivite.setId(id);
        return ResponseEntity.ok(service.save(rapportActivite));
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
