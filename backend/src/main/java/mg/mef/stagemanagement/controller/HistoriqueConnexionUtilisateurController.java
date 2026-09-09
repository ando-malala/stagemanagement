package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.HistoriqueConnexionUtilisateur;
import mg.mef.stagemanagement.service.HistoriqueConnexionUtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historique-connexions")
public class HistoriqueConnexionUtilisateurController {

    private final HistoriqueConnexionUtilisateurService service;

    public HistoriqueConnexionUtilisateurController(HistoriqueConnexionUtilisateurService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<HistoriqueConnexionUtilisateur>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoriqueConnexionUtilisateur> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HistoriqueConnexionUtilisateur> create(@RequestBody HistoriqueConnexionUtilisateur historiqueConnexionUtilisateur) {
        historiqueConnexionUtilisateur.setId(null);
        HistoriqueConnexionUtilisateur saved = service.save(historiqueConnexionUtilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistoriqueConnexionUtilisateur> update(
            @PathVariable Integer id,
            @RequestBody HistoriqueConnexionUtilisateur historiqueConnexionUtilisateur) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        historiqueConnexionUtilisateur.setId(id);
        return ResponseEntity.ok(service.save(historiqueConnexionUtilisateur));
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
