package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.SessionUtilisateur;
import mg.mef.stagemanagement.service.SessionUtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions-utilisateur")
public class SessionUtilisateurController {

    private final SessionUtilisateurService service;

    public SessionUtilisateurController(SessionUtilisateurService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SessionUtilisateur>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionUtilisateur> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SessionUtilisateur> create(@RequestBody SessionUtilisateur sessionUtilisateur) {
        sessionUtilisateur.setId(null);
        SessionUtilisateur saved = service.save(sessionUtilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionUtilisateur> update(
            @PathVariable Integer id,
            @RequestBody SessionUtilisateur sessionUtilisateur) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        sessionUtilisateur.setId(id);
        return ResponseEntity.ok(service.save(sessionUtilisateur));
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
