package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.EvaluationCritere;
import mg.mef.stagemanagement.repository.EvaluationCritereRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluationCritereService {

    private final EvaluationCritereRepository repository;

    public EvaluationCritereService(EvaluationCritereRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EvaluationCritere> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<EvaluationCritere> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public EvaluationCritere save(EvaluationCritere entity) {
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
