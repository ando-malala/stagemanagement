package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.Soutenance;
import mg.mef.stagemanagement.repository.SoutenanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SoutenanceService {

    private final SoutenanceRepository repository;

    public SoutenanceService(SoutenanceRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Soutenance> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Soutenance> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Soutenance save(Soutenance entity) {
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
