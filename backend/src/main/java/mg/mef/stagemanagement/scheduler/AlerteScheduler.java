
package mg.mef.stagemanagement.scheduler;

import mg.mef.stagemanagement.service.AlerteOrchestrationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AlerteScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(AlerteScheduler.class);

    private final AlerteOrchestrationService orchestrationService;

    public AlerteScheduler(
            AlerteOrchestrationService orchestrationService
    ) {
        this.orchestrationService = orchestrationService;
    }

    @Scheduled(
            cron = "0 0 10 * * *",
            zone = "Indian/Antananarivo"
    )
    public void executerVerificationQuotidienne() {

        logger.info("Début du contrôle quotidien des alertes");

        try {
            orchestrationService.executerControlesQuotidiens();

            logger.info("Contrôle quotidien des alertes terminé");

        } catch (Exception exception) {
            logger.error(
                    "Erreur pendant le contrôle quotidien des alertes",
                    exception
            );
        }
    }
}
