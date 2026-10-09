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
    ) {
        this.stagiaireRepository = stagiaireRepository;
        this.stageRepository = stageRepository;
        this.stageParticipantRepository = stageParticipantRepository;
        this.rapportActiviteRepository = rapportActiviteRepository;
        this.alerteRepository = alerteRepository;
    }

    public DashboardResumeResponse resume() {
        return new DashboardResumeResponse(
                stagiaireRepository.count(),
                stageRepository.count(),
                stageParticipantRepository.count(),
                rapportActiviteRepository.count(),
                alerteRepository.countByStatut(AlerteStatut.A_TRAITER),
                alerteRepository.countByStatut(AlerteStatut.TRAITEE)
        );
    }
}