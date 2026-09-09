package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.StageParticipant;
import mg.mef.stagemanagement.repository.StageParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StageParticipantService {

    private final StageParticipantRepository repository;

    public StageParticipantService(StageParticipantRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StageParticipant> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<StageParticipant> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public StageParticipant save(StageParticipant entity) {
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
