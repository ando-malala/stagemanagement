package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.RapportObservation;
import mg.mef.stagemanagement.repository.RapportObservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RapportObservationService {

    private final RapportObservationRepository repository;

    public RapportObservationService(RapportObservationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<RapportObservation> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<RapportObservation> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public RapportObservation save(RapportObservation entity) {
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
