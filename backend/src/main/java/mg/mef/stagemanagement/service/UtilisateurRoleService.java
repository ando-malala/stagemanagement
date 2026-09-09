package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.UtilisateurRole;
import mg.mef.stagemanagement.repository.UtilisateurRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurRoleService {

    private final UtilisateurRoleRepository repository;

    public UtilisateurRoleService(UtilisateurRoleRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<UtilisateurRole> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<UtilisateurRole> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public UtilisateurRole save(UtilisateurRole entity) {
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
