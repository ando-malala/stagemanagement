package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.UtilisateurRole;
import mg.mef.stagemanagement.service.UtilisateurRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs-role")
public class UtilisateurRoleController {

    private final UtilisateurRoleService service;

    public UtilisateurRoleController(UtilisateurRoleService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UtilisateurRole>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurRole> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UtilisateurRole> create(@RequestBody UtilisateurRole utilisateurRole) {
        utilisateurRole.setId(null);
        UtilisateurRole saved = service.save(utilisateurRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurRole> update(
            @PathVariable Integer id,
            @RequestBody UtilisateurRole utilisateurRole) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        utilisateurRole.setId(id);
        return ResponseEntity.ok(service.save(utilisateurRole));
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
