
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

        detectionService.detecterFinsStagesProches();

        detectionService.detecterStagesDepasses();

        detectionService.detecterRapportsManquants();

        detectionService.detecterValidationsEnRetard();

        detectionService.detecterDossiersIncomplets();

        detectionService.detecterEncadreursSurcharges();

        detectionService.detecterSoutenancesProches();
    }

}
