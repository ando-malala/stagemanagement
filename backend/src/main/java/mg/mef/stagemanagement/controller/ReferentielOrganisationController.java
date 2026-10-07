package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.referentiel.CadreStageRequest;
import mg.mef.stagemanagement.dto.referentiel.CadreStageResponse;
import mg.mef.stagemanagement.dto.referentiel.DepartementRequest;
import mg.mef.stagemanagement.dto.referentiel.DepartementResponse;
import mg.mef.stagemanagement.dto.referentiel.StructureAccueilRequest;
import mg.mef.stagemanagement.dto.referentiel.StructureAccueilResponse;
import mg.mef.stagemanagement.service.ReferentielOrganisationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referentiels/organisation")
public class ReferentielOrganisationController {

    private final ReferentielOrganisationService service;

    public ReferentielOrganisationController(
            ReferentielOrganisationService service
    ) {
        this.service = service;
    }

    // =========================
    // DÉPARTEMENTS
    // =========================

    @GetMapping("/departements")
    public ResponseEntity<List<DepartementResponse>> listerDepartements() {
        return ResponseEntity.ok(
                service.listerDepartements()
        );
    }

    @PostMapping("/departements")
    public ResponseEntity<DepartementResponse> creerDepartement(
            @RequestBody DepartementRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.creerDepartement(request));
    }

    @PatchMapping("/departements/{id}")
    public ResponseEntity<DepartementResponse> modifierDepartement(
            @PathVariable Integer id,
            @RequestBody DepartementRequest request
    ) {
        return ResponseEntity.ok(
                service.modifierDepartement(id, request)
        );
    }

    // =========================
    // STRUCTURES D'ACCUEIL
    // =========================

    @GetMapping("/structures-accueil")
    public ResponseEntity<List<StructureAccueilResponse>> listerStructuresAccueil() {
        return ResponseEntity.ok(
                service.listerStructuresAccueil()
        );
    }

    @PostMapping("/structures-accueil")
    public ResponseEntity<StructureAccueilResponse> creerStructureAccueil(
            @RequestBody StructureAccueilRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.creerStructureAccueil(request));
    }

    @PatchMapping("/structures-accueil/{id}")
    public ResponseEntity<StructureAccueilResponse> modifierStructureAccueil(
            @PathVariable Integer id,
            @RequestBody StructureAccueilRequest request
    ) {
        return ResponseEntity.ok(
                service.modifierStructureAccueil(id, request)
        );
    }

    // =========================
    // CADRES DE STAGE
    // =========================

    @GetMapping("/cadres-stage")
    public ResponseEntity<List<CadreStageResponse>> listerCadresStage() {
        return ResponseEntity.ok(
                service.listerCadresStage()
        );
    }

    @PostMapping("/cadres-stage")
    public ResponseEntity<CadreStageResponse> creerCadreStage(
            @RequestBody CadreStageRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.creerCadreStage(request));
    }

    @PatchMapping("/cadres-stage/{id}")
    public ResponseEntity<CadreStageResponse> modifierCadreStage(
            @PathVariable Integer id,
            @RequestBody CadreStageRequest request
    ) {
        return ResponseEntity.ok(
                service.modifierCadreStage(id, request)
        );
    }
}