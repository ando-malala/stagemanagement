package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.ResponsableDepartement;
import mg.mef.stagemanagement.repository.ResponsableDepartementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ResponsableDepartementService {

    private final ResponsableDepartementRepository repository;

    public ResponsableDepartementService(ResponsableDepartementRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ResponsableDepartement> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ResponsableDepartement> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public ResponsableDepartement save(ResponsableDepartement entity) {
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
