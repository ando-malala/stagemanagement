package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.UtilisateurStatutHistorique;
import mg.mef.stagemanagement.service.UtilisateurStatutHistoriqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateur-statut-historique")
public class UtilisateurStatutHistoriqueController {

    private final UtilisateurStatutHistoriqueService service;

    public UtilisateurStatutHistoriqueController(UtilisateurStatutHistoriqueService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UtilisateurStatutHistorique>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurStatutHistorique> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UtilisateurStatutHistorique> create(@RequestBody UtilisateurStatutHistorique utilisateurStatutHistorique) {
        utilisateurStatutHistorique.setId(null);
        UtilisateurStatutHistorique saved = service.save(utilisateurStatutHistorique);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurStatutHistorique> update(
            @PathVariable Integer id,
            @RequestBody UtilisateurStatutHistorique utilisateurStatutHistorique) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        utilisateurStatutHistorique.setId(id);
        return ResponseEntity.ok(service.save(utilisateurStatutHistorique));
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
