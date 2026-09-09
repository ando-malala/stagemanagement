package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.StageParticipantFichier;
import mg.mef.stagemanagement.repository.StageParticipantFichierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StageParticipantFichierService {

    private final StageParticipantFichierRepository repository;

    public StageParticipantFichierService(StageParticipantFichierRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StageParticipantFichier> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<StageParticipantFichier> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public StageParticipantFichier save(StageParticipantFichier entity) {
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
