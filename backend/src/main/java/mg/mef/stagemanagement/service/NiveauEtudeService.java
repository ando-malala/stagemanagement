package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.NiveauEtude;
import mg.mef.stagemanagement.repository.NiveauEtudeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NiveauEtudeService {

    private final NiveauEtudeRepository repository;

    public NiveauEtudeService(NiveauEtudeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<NiveauEtude> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<NiveauEtude> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public NiveauEtude save(NiveauEtude entity) {
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
