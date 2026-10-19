
package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.service.AlerteDetectionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/alertes/detection")
public class AlerteDetectionController {

    private final AlerteDetectionService detectionService;

    public AlerteDetectionController(
            AlerteDetectionService detectionService
    ) {
        this.detectionService = detectionService;
    }

    @PostMapping("/stages-depasses")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_STAGE')")
    public ResponseEntity<Map<String, Object>> detecterStagesDepasses() {

        int nouvellesAlertes = detectionService.detecterStagesDepasses();

        return ResponseEntity.ok(
                Map.of(
                        "message", "Vérification des stages dépassés terminée",
                        "nouvellesAlertes", nouvellesAlertes
                )
        );
    }

    @PostMapping("/fins-stages-proches")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_STAGE')")
    public ResponseEntity<Map<String, Object>> detecterFinsStagesProches() {

        int nouvellesAlertes = detectionService.detecterFinsStagesProches();

        return ResponseEntity.ok(
                Map.of(
                        "message", "Vérification des fins de stage proches terminée",
                        "nouvellesAlertes", nouvellesAlertes
                )
        );
    }
}
