package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.TypePieceJointe;
import mg.mef.stagemanagement.repository.TypePieceJointeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TypePieceJointeService {

    private final TypePieceJointeRepository repository;

    public TypePieceJointeService(TypePieceJointeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TypePieceJointe> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<TypePieceJointe> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public TypePieceJointe save(TypePieceJointe entity) {
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
