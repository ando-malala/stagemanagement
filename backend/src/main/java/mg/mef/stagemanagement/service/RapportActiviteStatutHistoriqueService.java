package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.RapportActiviteStatutHistorique;
import mg.mef.stagemanagement.repository.RapportActiviteStatutHistoriqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RapportActiviteStatutHistoriqueService {

    private final RapportActiviteStatutHistoriqueRepository repository;

    public RapportActiviteStatutHistoriqueService(RapportActiviteStatutHistoriqueRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<RapportActiviteStatutHistorique> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<RapportActiviteStatutHistorique> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public RapportActiviteStatutHistorique save(RapportActiviteStatutHistorique entity) {
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
