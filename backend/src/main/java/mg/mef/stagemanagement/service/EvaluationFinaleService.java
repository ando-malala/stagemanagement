package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.EvaluationFinale;
import mg.mef.stagemanagement.repository.EvaluationFinaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluationFinaleService {

    private final EvaluationFinaleRepository repository;

    public EvaluationFinaleService(EvaluationFinaleRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EvaluationFinale> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<EvaluationFinale> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public EvaluationFinale save(EvaluationFinale entity) {
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
