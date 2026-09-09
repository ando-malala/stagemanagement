package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.EncadreurStatutHistorique;
import mg.mef.stagemanagement.repository.EncadreurStatutHistoriqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EncadreurStatutHistoriqueService {

    private final EncadreurStatutHistoriqueRepository repository;

    public EncadreurStatutHistoriqueService(EncadreurStatutHistoriqueRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EncadreurStatutHistorique> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<EncadreurStatutHistorique> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public EncadreurStatutHistorique save(EncadreurStatutHistorique entity) {
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
