package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.Stage;
import mg.mef.stagemanagement.repository.StageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StageService {

    private final StageRepository repository;

    public StageService(StageRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Stage> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Stage> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Stage save(Stage entity) {
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
