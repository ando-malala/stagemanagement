package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.NiveauEtude;
import mg.mef.stagemanagement.service.NiveauEtudeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/niveaux-etude")
public class NiveauEtudeController {

    private final NiveauEtudeService service;

    public NiveauEtudeController(NiveauEtudeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<NiveauEtude>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NiveauEtude> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NiveauEtude> create(@RequestBody NiveauEtude niveauEtude) {
        niveauEtude.setId(null);
        NiveauEtude saved = service.save(niveauEtude);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NiveauEtude> update(
            @PathVariable Integer id,
            @RequestBody NiveauEtude niveauEtude) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        niveauEtude.setId(id);
        return ResponseEntity.ok(service.save(niveauEtude));
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
