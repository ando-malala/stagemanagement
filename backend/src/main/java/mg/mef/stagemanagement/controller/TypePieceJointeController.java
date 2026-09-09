package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.TypePieceJointe;
import mg.mef.stagemanagement.service.TypePieceJointeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/types-piece-jointe")
public class TypePieceJointeController {

    private final TypePieceJointeService service;

    public TypePieceJointeController(TypePieceJointeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TypePieceJointe>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypePieceJointe> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TypePieceJointe> create(@RequestBody TypePieceJointe typePieceJointe) {
        typePieceJointe.setId(null);
        TypePieceJointe saved = service.save(typePieceJointe);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypePieceJointe> update(
            @PathVariable Integer id,
            @RequestBody TypePieceJointe typePieceJointe) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        typePieceJointe.setId(id);
        return ResponseEntity.ok(service.save(typePieceJointe));
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
