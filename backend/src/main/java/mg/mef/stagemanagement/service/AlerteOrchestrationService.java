
package mg.mef.stagemanagement.service;

import org.springframework.stereotype.Service;

@Service
public class AlerteOrchestrationService {

    private final AlerteDetectionService detectionService;

    public AlerteOrchestrationService(
            AlerteDetectionService detectionService
    ) {
        this.detectionService = detectionService;
    }

    public void executerControlesQuotidiens() {

        // 1. Détecter les stages proches de leur fin
        detectionService.detecterFinsStagesProches();

        // 2. Détecter les stages dépassés
        detectionService.detecterStagesDepasses();

        // 3. Détecter les rapports hebdomadaires manquants
        detectionService.detecterRapportsManquants();
    }
}
