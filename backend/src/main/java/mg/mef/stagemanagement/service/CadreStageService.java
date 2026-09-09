package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.CadreStage;
import mg.mef.stagemanagement.repository.CadreStageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CadreStageService {

    private final CadreStageRepository repository;

    public CadreStageService(CadreStageRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CadreStage> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<CadreStage> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public CadreStage save(CadreStage entity) {
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
