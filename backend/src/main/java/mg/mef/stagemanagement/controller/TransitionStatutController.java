package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.TransitionStatut;
import mg.mef.stagemanagement.service.TransitionStatutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transitions-statut")
public class TransitionStatutController {

    private final TransitionStatutService service;

    public TransitionStatutController(TransitionStatutService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TransitionStatut>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransitionStatut> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TransitionStatut> create(@RequestBody TransitionStatut transitionStatut) {
        transitionStatut.setId(null);
        TransitionStatut saved = service.save(transitionStatut);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransitionStatut> update(
            @PathVariable Integer id,
            @RequestBody TransitionStatut transitionStatut) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        transitionStatut.setId(id);
        return ResponseEntity.ok(service.save(transitionStatut));
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
