package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.dashboard.DashboardResumeResponse;
import mg.mef.stagemanagement.model.enums.AlerteStatut;
import mg.mef.stagemanagement.repository.AlerteRepository;
import mg.mef.stagemanagement.repository.RapportActiviteRepository;
import mg.mef.stagemanagement.repository.StageParticipantRepository;
import mg.mef.stagemanagement.repository.StageRepository;
import mg.mef.stagemanagement.repository.StagiaireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {
    private final DossierAccessService access;

    private final StagiaireRepository stagiaireRepository;
    private final StageRepository stageRepository;
    private final StageParticipantRepository stageParticipantRepository;
    private final RapportActiviteRepository rapportActiviteRepository;
    private final AlerteRepository alerteRepository;

    public DashboardService(
            StagiaireRepository stagiaireRepository,
            StageRepository stageRepository,
            StageParticipantRepository stageParticipantRepository,
            RapportActiviteRepository rapportActiviteRepository,
            AlerteRepository alerteRepository
    , DossierAccessService access) {
        this.access = access;
        this.stagiaireRepository = stagiaireRepository;
        this.stageRepository = stageRepository;
        this.stageParticipantRepository = stageParticipantRepository;
        this.rapportActiviteRepository = rapportActiviteRepository;
        this.alerteRepository = alerteRepository;
    }

    public DashboardResumeResponse resume() {
        return new DashboardResumeResponse(
                stagiaireRepository.findAll().stream().filter(access::readTrainee).count(),
                stageRepository.findAll().stream().filter(access::readStage).count(),
                stageParticipantRepository.findAll().stream().filter(access::readParticipation).count(),
                rapportActiviteRepository.findAll().stream().filter(access::readReport).count(),
                alerteRepository.findByStatutOrderByDateDetectionDesc(AlerteStatut.A_TRAITER).stream().filter(access::readAlert).filter(AlerteClotureService::encorePertinente).count(),
                alerteRepository.findByStatutOrderByDateDetectionDesc(AlerteStatut.TRAITEE).stream().filter(access::readAlert).count()
        );
    }
}
