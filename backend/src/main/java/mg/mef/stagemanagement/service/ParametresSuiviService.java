package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.ParametresSuivi;
import mg.mef.stagemanagement.repository.ParametresSuiviRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ParametresSuiviService {

    private final ParametresSuiviRepository repository;

    public ParametresSuiviService(ParametresSuiviRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ParametresSuivi> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ParametresSuivi> findById(Short id) {
        return repository.findById(id);
    }

    @Transactional
    public ParametresSuivi save(ParametresSuivi entity) {
        return repository.save(entity);
    }

    @Transactional
    public void deleteById(Short id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Short id) {
        return repository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }
}
