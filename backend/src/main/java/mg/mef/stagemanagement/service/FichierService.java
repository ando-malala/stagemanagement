package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.Fichier;
import mg.mef.stagemanagement.repository.FichierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FichierService {

    private final FichierRepository repository;

    public FichierService(FichierRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Fichier> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Fichier> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Fichier save(Fichier entity) {
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
