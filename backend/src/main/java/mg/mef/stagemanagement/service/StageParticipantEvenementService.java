package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.StageParticipantEvenement;
import mg.mef.stagemanagement.repository.StageParticipantEvenementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StageParticipantEvenementService {

    private final StageParticipantEvenementRepository repository;

    public StageParticipantEvenementService(StageParticipantEvenementRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StageParticipantEvenement> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<StageParticipantEvenement> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public StageParticipantEvenement save(StageParticipantEvenement entity) {
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
