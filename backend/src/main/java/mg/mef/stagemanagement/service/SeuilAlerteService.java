package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.SeuilAlerte;
import mg.mef.stagemanagement.repository.SeuilAlerteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SeuilAlerteService {

    private final SeuilAlerteRepository repository;

    public SeuilAlerteService(SeuilAlerteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<SeuilAlerte> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<SeuilAlerte> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public SeuilAlerte save(SeuilAlerte entity) {
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
