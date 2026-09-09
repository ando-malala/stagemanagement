package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.SessionUtilisateur;
import mg.mef.stagemanagement.repository.SessionUtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SessionUtilisateurService {

    private final SessionUtilisateurRepository repository;

    public SessionUtilisateurService(SessionUtilisateurRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<SessionUtilisateur> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<SessionUtilisateur> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public SessionUtilisateur save(SessionUtilisateur entity) {
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
