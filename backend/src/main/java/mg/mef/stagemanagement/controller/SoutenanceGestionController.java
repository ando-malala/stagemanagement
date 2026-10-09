package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.soutenance.SoutenanceRequest;
import mg.mef.stagemanagement.dto.soutenance.SoutenanceResponse;
import mg.mef.stagemanagement.service.SoutenanceGestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/soutenances")
public class SoutenanceGestionController {

    private final SoutenanceGestionService service;

    public SoutenanceGestionController(SoutenanceGestionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SoutenanceResponse>> listerSoutenances() {
        return ResponseEntity.ok(service.listerSoutenances());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SoutenanceResponse> trouverSoutenance(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.trouverSoutenance(id));
    }

    @PostMapping
    public ResponseEntity<SoutenanceResponse> creerSoutenance(
            @RequestBody SoutenanceRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.creerSoutenance(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SoutenanceResponse> modifierSoutenance(
            @PathVariable Integer id,
            @RequestBody SoutenanceRequest request
    ) {
        return ResponseEntity.ok(
                service.modifierSoutenance(id, request)
        );
    }
}