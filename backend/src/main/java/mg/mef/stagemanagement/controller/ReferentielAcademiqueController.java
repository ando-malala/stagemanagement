package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.referentiel.ReferentielRequest;
import mg.mef.stagemanagement.dto.referentiel.ReferentielResponse;
import mg.mef.stagemanagement.service.ReferentielAcademiqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referentiels/academiques")
public class ReferentielAcademiqueController {

    private final ReferentielAcademiqueService referentielAcademiqueService;

    public ReferentielAcademiqueController(
            ReferentielAcademiqueService referentielAcademiqueService
    ) {
        this.referentielAcademiqueService = referentielAcademiqueService;
    }

    // =========================
    // ÉTABLISSEMENTS
    // =========================

    @GetMapping("/etablissements")
    public ResponseEntity<List<ReferentielResponse>> listerEtablissements() {
        return ResponseEntity.ok(
                referentielAcademiqueService.listerEtablissements()
        );
    }

    @PostMapping("/etablissements")
    public ResponseEntity<ReferentielResponse> creerEtablissement(
            @RequestBody ReferentielRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(referentielAcademiqueService.creerEtablissement(request));
    }

    @PatchMapping("/etablissements/{id}")
    public ResponseEntity<ReferentielResponse> modifierEtablissement(
            @PathVariable Integer id,
            @RequestBody ReferentielRequest request
    ) {
        return ResponseEntity.ok(
                referentielAcademiqueService.modifierEtablissement(id, request)
        );
    }

    // =========================
    // FILIÈRES
    // =========================

    @GetMapping("/filieres")
    public ResponseEntity<List<ReferentielResponse>> listerFilieres() {
        return ResponseEntity.ok(
                referentielAcademiqueService.listerFilieres()
        );
    }

    @PostMapping("/filieres")
    public ResponseEntity<ReferentielResponse> creerFiliere(
            @RequestBody ReferentielRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(referentielAcademiqueService.creerFiliere(request));
    }

    @PatchMapping("/filieres/{id}")
    public ResponseEntity<ReferentielResponse> modifierFiliere(
            @PathVariable Integer id,
            @RequestBody ReferentielRequest request
    ) {
        return ResponseEntity.ok(
                referentielAcademiqueService.modifierFiliere(id, request)
        );
    }

    // =========================
    // NIVEAUX D'ÉTUDE
    // =========================

    @GetMapping("/niveaux-etude")
    public ResponseEntity<List<ReferentielResponse>> listerNiveauxEtude() {
        return ResponseEntity.ok(
                referentielAcademiqueService.listerNiveauxEtude()
        );
    }

    @PostMapping("/niveaux-etude")
    public ResponseEntity<ReferentielResponse> creerNiveauEtude(
            @RequestBody ReferentielRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(referentielAcademiqueService.creerNiveauEtude(request));
    }

    @PatchMapping("/niveaux-etude/{id}")
    public ResponseEntity<ReferentielResponse> modifierNiveauEtude(
            @PathVariable Integer id,
            @RequestBody ReferentielRequest request
    ) {
        return ResponseEntity.ok(
                referentielAcademiqueService.modifierNiveauEtude(id, request)
        );
    }

    // =========================
    // MOIS DE STAGE
    // =========================

    @GetMapping("/mois-stage")
    public ResponseEntity<List<ReferentielResponse>> listerMoisStage() {
        return ResponseEntity.ok(
                referentielAcademiqueService.listerMoisStage()
        );
    }

    @PostMapping("/mois-stage")
    public ResponseEntity<ReferentielResponse> creerMoisStage(
            @RequestBody ReferentielRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(referentielAcademiqueService.creerMoisStage(request));
    }

    @PatchMapping("/mois-stage/{id}")
    public ResponseEntity<ReferentielResponse> modifierMoisStage(
            @PathVariable Integer id,
            @RequestBody ReferentielRequest request
    ) {
        return ResponseEntity.ok(
                referentielAcademiqueService.modifierMoisStage(id, request)
        );
    }
}