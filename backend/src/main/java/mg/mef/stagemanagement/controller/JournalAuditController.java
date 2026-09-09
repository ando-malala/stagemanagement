package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.model.JournalAudit;
import mg.mef.stagemanagement.service.JournalAuditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journal-audit")
public class JournalAuditController {

    private final JournalAuditService service;

    public JournalAuditController(JournalAuditService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<JournalAudit>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalAudit> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<JournalAudit> create(@RequestBody JournalAudit journalAudit) {
        journalAudit.setId(null);
        JournalAudit saved = service.save(journalAudit);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JournalAudit> update(
            @PathVariable Integer id,
            @RequestBody JournalAudit journalAudit) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        journalAudit.setId(id);
        return ResponseEntity.ok(service.save(journalAudit));
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
