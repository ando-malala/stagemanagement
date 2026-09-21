package mg.mef.stagemanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.mef.stagemanagement.dto.utilisateur.UtilisateurListResponse;
import mg.mef.stagemanagement.model.Utilisateur;
import mg.mef.stagemanagement.repository.UtilisateurRepository;

@Service
public class UtilisateurService {

    private final UtilisateurRepository repository;

    public UtilisateurService(UtilisateurRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Utilisateur> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Utilisateur save(Utilisateur entity) {
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

    @Transactional(readOnly = true)
    public List<UtilisateurListResponse> listerUtilisateurs() {

        return repository.findAllAvecStatut()
                .stream()
                .map(utilisateur -> new UtilisateurListResponse(
                        utilisateur.getId(),
                        utilisateur.getIdentifiant(),
                        utilisateur.getStatut().getCode()
                ))
                .toList();
    }
}
