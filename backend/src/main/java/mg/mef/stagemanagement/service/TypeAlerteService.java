package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.TypeAlerte;
import mg.mef.stagemanagement.repository.TypeAlerteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TypeAlerteService {

    private final TypeAlerteRepository repository;

    public TypeAlerteService(TypeAlerteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TypeAlerte> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<TypeAlerte> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public TypeAlerte save(TypeAlerte entity) {
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
