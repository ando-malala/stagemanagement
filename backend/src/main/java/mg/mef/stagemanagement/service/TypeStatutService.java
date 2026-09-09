package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.TypeStatut;
import mg.mef.stagemanagement.repository.TypeStatutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TypeStatutService {

    private final TypeStatutRepository repository;

    public TypeStatutService(TypeStatutRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TypeStatut> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<TypeStatut> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public TypeStatut save(TypeStatut entity) {
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
