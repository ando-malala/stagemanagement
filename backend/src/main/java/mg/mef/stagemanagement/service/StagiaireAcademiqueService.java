package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.StagiaireAcademique;
import mg.mef.stagemanagement.repository.StagiaireAcademiqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StagiaireAcademiqueService {

    private final StagiaireAcademiqueRepository repository;

    public StagiaireAcademiqueService(StagiaireAcademiqueRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StagiaireAcademique> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<StagiaireAcademique> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public StagiaireAcademique save(StagiaireAcademique entity) {
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
