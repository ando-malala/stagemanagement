package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.Etablissement;
import mg.mef.stagemanagement.repository.EtablissementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EtablissementService {

    private final EtablissementRepository repository;

    public EtablissementService(EtablissementRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Etablissement> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Etablissement> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Etablissement save(Etablissement entity) {
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
