package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.TransitionStatut;
import mg.mef.stagemanagement.repository.TransitionStatutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TransitionStatutService {

    private final TransitionStatutRepository repository;

    public TransitionStatutService(TransitionStatutRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TransitionStatut> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<TransitionStatut> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public TransitionStatut save(TransitionStatut entity) {
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
