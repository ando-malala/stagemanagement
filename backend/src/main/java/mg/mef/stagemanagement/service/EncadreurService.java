package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.Encadreur;
import mg.mef.stagemanagement.repository.EncadreurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EncadreurService {

    private final EncadreurRepository repository;

    public EncadreurService(EncadreurRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Encadreur> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Encadreur> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Encadreur save(Encadreur entity) {
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
