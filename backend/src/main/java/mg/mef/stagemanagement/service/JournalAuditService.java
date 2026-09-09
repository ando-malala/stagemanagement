package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.JournalAudit;
import mg.mef.stagemanagement.repository.JournalAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class JournalAuditService {

    private final JournalAuditRepository repository;

    public JournalAuditService(JournalAuditRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<JournalAudit> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<JournalAudit> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public JournalAudit save(JournalAudit entity) {
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
