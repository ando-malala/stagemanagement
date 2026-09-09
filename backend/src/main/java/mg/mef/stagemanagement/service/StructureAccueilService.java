package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.StructureAccueil;
import mg.mef.stagemanagement.repository.StructureAccueilRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StructureAccueilService {

    private final StructureAccueilRepository repository;

    public StructureAccueilService(StructureAccueilRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StructureAccueil> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<StructureAccueil> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public StructureAccueil save(StructureAccueil entity) {
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
