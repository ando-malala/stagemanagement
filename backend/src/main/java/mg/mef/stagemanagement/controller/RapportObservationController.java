package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.RapportObservation;
import mg.mef.stagemanagement.service.RapportObservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rapport-observations")
public class RapportObservationController {

    private final RapportObservationService service;

    public RapportObservationController(RapportObservationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RapportObservation>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RapportObservation> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RapportObservation> create(@RequestBody RapportObservation rapportObservation) {
        rapportObservation.setId(null);
        RapportObservation saved = service.save(rapportObservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RapportObservation> update(
            @PathVariable Integer id,
            @RequestBody RapportObservation rapportObservation) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        rapportObservation.setId(id);
        return ResponseEntity.ok(service.save(rapportObservation));
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
