package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.audit.JournalAuditResponse;
import mg.mef.stagemanagement.service.JournalAuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journal-audit")
public class JournalAuditController {

    private final JournalAuditService journalAuditService;

    public JournalAuditController(
            JournalAuditService journalAuditService
    ) {
        this.journalAuditService = journalAuditService;
    }

    @GetMapping
    public ResponseEntity<List<JournalAuditResponse>> listerJournal() {
        return ResponseEntity.ok(
                journalAuditService.listerJournal()
        );
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<JournalAuditResponse>> listerParUtilisateur(
            @PathVariable Integer utilisateurId
    ) {
        return ResponseEntity.ok(
                journalAuditService.listerParUtilisateur(utilisateurId)
        );
    }

    @GetMapping("/entite/{entite}/{entiteId}")
    public ResponseEntity<List<JournalAuditResponse>> listerParEntite(
            @PathVariable String entite,
            @PathVariable Integer entiteId
    ) {
        return ResponseEntity.ok(
                journalAuditService.listerParEntite(entite, entiteId)
        );
    }
}