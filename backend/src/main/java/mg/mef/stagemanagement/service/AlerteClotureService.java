package mg.mef.stagemanagement.service;

import java.time.LocalDateTime;
import mg.mef.stagemanagement.model.Alerte;
import mg.mef.stagemanagement.model.StageParticipant;
import mg.mef.stagemanagement.model.enums.AlerteStatut;
import mg.mef.stagemanagement.repository.AlerteRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlerteClotureService {
    private final AlerteRepository alertes;

    public AlerteClotureService(AlerteRepository alertes) { this.alertes = alertes; }

    public static boolean cloturee(StageParticipant participation) {
        return participation != null && (participation.getDateCloture() != null
                || (participation.getStatut() != null && "CLOTUREE".equals(participation.getStatut().getCode())));
    }

    public static boolean encorePertinente(Alerte alerte) {
        return !cloturee(alerte.getStageParticipant())
                && (alerte.getRapport() == null || !cloturee(alerte.getRapport().getStageParticipant()))
                && (alerte.getSoutenance() == null || !cloturee(alerte.getSoutenance().getStageParticipant()));
    }

    @Transactional
    public void expirerPourParticipation(Integer participationId) {
        alertes.findByParticipationAndStatut(participationId, AlerteStatut.A_TRAITER).forEach(this::expirer);
    }

    // Also reconcile alerts left active by versions that did not expire them on closure.
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void expirerAlertesDejaCloturees() {
        alertes.findByStatutOrderByDateDetectionDesc(AlerteStatut.A_TRAITER).stream()
                .filter(alerte -> !encorePertinente(alerte)).forEach(this::expirer);
    }

    private void expirer(Alerte alerte) {
        alerte.setStatut(AlerteStatut.EXPIREE);
        alerte.setDateTraitement(LocalDateTime.now());
        alerte.setTraitePar(null);
        alertes.save(alerte);
    }
}
