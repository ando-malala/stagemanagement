package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.Statut;
import mg.mef.stagemanagement.repository.StatutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StatutService {

    private final StatutRepository repository;

    public StatutService(StatutRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Statut> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Statut> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Statut save(Statut entity) {
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
