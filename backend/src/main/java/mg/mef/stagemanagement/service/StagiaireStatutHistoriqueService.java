package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.StagiaireStatutHistorique;
import mg.mef.stagemanagement.repository.StagiaireStatutHistoriqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StagiaireStatutHistoriqueService {

    private final StagiaireStatutHistoriqueRepository repository;

    public StagiaireStatutHistoriqueService(StagiaireStatutHistoriqueRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StagiaireStatutHistorique> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<StagiaireStatutHistorique> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public StagiaireStatutHistorique save(StagiaireStatutHistorique entity) {
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
