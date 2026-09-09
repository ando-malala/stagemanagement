package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.TypeEvenementParticipation;
import mg.mef.stagemanagement.repository.TypeEvenementParticipationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TypeEvenementParticipationService {

    private final TypeEvenementParticipationRepository repository;

    public TypeEvenementParticipationService(TypeEvenementParticipationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TypeEvenementParticipation> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<TypeEvenementParticipation> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public TypeEvenementParticipation save(TypeEvenementParticipation entity) {
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
