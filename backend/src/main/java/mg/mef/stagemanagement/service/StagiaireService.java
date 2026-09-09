package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.Stagiaire;
import mg.mef.stagemanagement.repository.StagiaireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StagiaireService {

    private final StagiaireRepository repository;

    public StagiaireService(StagiaireRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Stagiaire> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Stagiaire> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Stagiaire save(Stagiaire entity) {
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
