package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.stagiaire.StagiaireAcademiqueRequest;
import mg.mef.stagemanagement.dto.stagiaire.StagiaireAcademiqueResponse;
import mg.mef.stagemanagement.dto.stagiaire.StagiaireRequest;
import mg.mef.stagemanagement.dto.stagiaire.StagiaireResponse;
import mg.mef.stagemanagement.service.StagiaireGestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stagiaires")
public class StagiaireGestionController {

    private final StagiaireGestionService stagiaireGestionService;

    public StagiaireGestionController(
            StagiaireGestionService stagiaireGestionService
    ) {
        this.stagiaireGestionService = stagiaireGestionService;
    }

    @GetMapping
    public ResponseEntity<List<StagiaireResponse>> listerStagiaires() {
        return ResponseEntity.ok(
                stagiaireGestionService.listerStagiaires()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StagiaireResponse> trouverStagiaire(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                stagiaireGestionService.trouverStagiaire(id)
        );
    }

    @PostMapping
    public ResponseEntity<StagiaireResponse> creerStagiaire(
            @RequestBody StagiaireRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(stagiaireGestionService.creerStagiaire(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StagiaireResponse> modifierStagiaire(
            @PathVariable Integer id,
            @RequestBody StagiaireRequest request
    ) {
        return ResponseEntity.ok(
                stagiaireGestionService.modifierStagiaire(id, request)
        );
    }

    @GetMapping("/{id}/academiques")
    public ResponseEntity<List<StagiaireAcademiqueResponse>> listerAcademiques(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                stagiaireGestionService.listerAcademiques(id)
        );
    }

    @PostMapping("/{id}/academiques")
    public ResponseEntity<StagiaireAcademiqueResponse> ajouterAcademique(
            @PathVariable Integer id,
            @RequestBody StagiaireAcademiqueRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(stagiaireGestionService.ajouterAcademique(id, request));
    }
}