package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.UtilisateurStatutHistorique;
import mg.mef.stagemanagement.repository.UtilisateurStatutHistoriqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurStatutHistoriqueService {

    private final UtilisateurStatutHistoriqueRepository repository;

    public UtilisateurStatutHistoriqueService(UtilisateurStatutHistoriqueRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<UtilisateurStatutHistorique> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<UtilisateurStatutHistorique> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public UtilisateurStatutHistorique save(UtilisateurStatutHistorique entity) {
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
