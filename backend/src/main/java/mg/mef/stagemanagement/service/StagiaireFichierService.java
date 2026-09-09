package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.StagiaireFichier;
import mg.mef.stagemanagement.repository.StagiaireFichierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StagiaireFichierService {

    private final StagiaireFichierRepository repository;

    public StagiaireFichierService(StagiaireFichierRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StagiaireFichier> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<StagiaireFichier> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public StagiaireFichier save(StagiaireFichier entity) {
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
