package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.Departement;
import mg.mef.stagemanagement.repository.DepartementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DepartementService {

    private final DepartementRepository repository;

    public DepartementService(DepartementRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Departement> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Departement> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Departement save(Departement entity) {
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
