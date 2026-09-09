package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.TypeEvenementParticipation;
import mg.mef.stagemanagement.service.TypeEvenementParticipationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/types-evenement-participation")
public class TypeEvenementParticipationController {

    private final TypeEvenementParticipationService service;

    public TypeEvenementParticipationController(TypeEvenementParticipationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TypeEvenementParticipation>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeEvenementParticipation> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TypeEvenementParticipation> create(@RequestBody TypeEvenementParticipation typeEvenementParticipation) {
        typeEvenementParticipation.setId(null);
        TypeEvenementParticipation saved = service.save(typeEvenementParticipation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeEvenementParticipation> update(
            @PathVariable Integer id,
            @RequestBody TypeEvenementParticipation typeEvenementParticipation) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        typeEvenementParticipation.setId(id);
        return ResponseEntity.ok(service.save(typeEvenementParticipation));
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
