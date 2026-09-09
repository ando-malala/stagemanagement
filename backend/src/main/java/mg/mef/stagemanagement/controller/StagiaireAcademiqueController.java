package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.StagiaireAcademique;
import mg.mef.stagemanagement.service.StagiaireAcademiqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stagiaires-academiques")
public class StagiaireAcademiqueController {

    private final StagiaireAcademiqueService service;

    public StagiaireAcademiqueController(StagiaireAcademiqueService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<StagiaireAcademique>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StagiaireAcademique> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StagiaireAcademique> create(@RequestBody StagiaireAcademique stagiaireAcademique) {
        stagiaireAcademique.setId(null);
        StagiaireAcademique saved = service.save(stagiaireAcademique);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StagiaireAcademique> update(
            @PathVariable Integer id,
            @RequestBody StagiaireAcademique stagiaireAcademique) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stagiaireAcademique.setId(id);
        return ResponseEntity.ok(service.save(stagiaireAcademique));
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
