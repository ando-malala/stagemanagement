package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.StageParticipantStatutHistorique;
import mg.mef.stagemanagement.repository.StageParticipantStatutHistoriqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StageParticipantStatutHistoriqueService {

    private final StageParticipantStatutHistoriqueRepository repository;

    public StageParticipantStatutHistoriqueService(StageParticipantStatutHistoriqueRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StageParticipantStatutHistorique> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<StageParticipantStatutHistorique> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public StageParticipantStatutHistorique save(StageParticipantStatutHistorique entity) {
        return repository.save(entity);
    }

    @Transactional
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }
}
