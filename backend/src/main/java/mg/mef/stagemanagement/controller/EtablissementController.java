package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.Etablissement;
import mg.mef.stagemanagement.service.EtablissementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etablissements")
public class EtablissementController {

    private final EtablissementService service;

    public EtablissementController(EtablissementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Etablissement>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etablissement> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Etablissement> create(@RequestBody Etablissement etablissement) {
        etablissement.setId(null);
        Etablissement saved = service.save(etablissement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Etablissement> update(
            @PathVariable Integer id,
            @RequestBody Etablissement etablissement) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        etablissement.setId(id);
        return ResponseEntity.ok(service.save(etablissement));
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
