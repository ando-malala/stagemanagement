
package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.service.AlerteDetectionService;
import mg.mef.stagemanagement.service.AlerteOrchestrationService;

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
        private final AlerteOrchestrationService orchestrationService;

        public AlerteDetectionController(
                AlerteDetectionService detectionService,
                AlerteOrchestrationService orchestrationService
        ) {
                this.detectionService = detectionService;
                this.orchestrationService = orchestrationService;
        }

        @PostMapping("/stages-depasses")
        @PreAuthorize("hasRole('ADMIN')")
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
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Map<String, Object>> detecterFinsStagesProches() {

                int nouvellesAlertes = detectionService.detecterFinsStagesProches();

                return ResponseEntity.ok(
                        Map.of(
                                "message", "Vérification des fins de stage proches terminée",
                                "nouvellesAlertes", nouvellesAlertes
                        )
                );
        }

    
        @PostMapping("/rapports-manquants")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Map<String, Object>> detecterRapportsManquants() {

                int nouvellesAlertes = detectionService.detecterRapportsManquants();

                return ResponseEntity.ok(
                        Map.of(
                                "message", "Vérification des rapports manquants terminée",
                                "nouvellesAlertes", nouvellesAlertes
                        )
                );
        }

                
        @PostMapping("/test-orchestration")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Map<String, Object>> testerOrchestration() {

        orchestrationService.executerControlesQuotidiens();

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Exécution manuelle des contrôles quotidiens terminée"
                )
        );
        }


}
