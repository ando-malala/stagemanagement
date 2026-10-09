package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.audit.JournalAuditResponse;
import mg.mef.stagemanagement.model.JournalAudit;
import mg.mef.stagemanagement.model.Utilisateur;
import mg.mef.stagemanagement.repository.JournalAuditRepository;
import mg.mef.stagemanagement.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class JournalAuditService {

    private final JournalAuditRepository journalAuditRepository;
    private final UtilisateurRepository utilisateurRepository;

    public JournalAuditService(
            JournalAuditRepository journalAuditRepository,
            UtilisateurRepository utilisateurRepository
    ) {
        this.journalAuditRepository = journalAuditRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<JournalAuditResponse> listerJournal() {
        return journalAuditRepository.findAllByOrderByDateActionDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<JournalAuditResponse> listerParUtilisateur(
            Integer utilisateurId
    ) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Utilisateur introuvable"
            );
        }

        return journalAuditRepository
                .findByUtilisateur_IdOrderByDateActionDesc(utilisateurId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<JournalAuditResponse> listerParEntite(
            String entite,
            Integer entiteId
    ) {
        if (entite == null || entite.trim().isEmpty() || entiteId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'entité et son identifiant sont obligatoires"
            );
        }

        return journalAuditRepository
                .findByEntiteAndEntiteIdOrderByDateActionDesc(
                        entite.trim(),
                        entiteId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void enregistrer(
            Integer utilisateurId,
            String action,
            String entite,
            Integer entiteId,
            Map<String, Object> anciennesValeurs,
            Map<String, Object> nouvellesValeurs
    ) {
        if (action == null || action.trim().isEmpty()) {
            return;
        }

        if (entite == null || entite.trim().isEmpty()) {
            return;
        }

        JournalAudit journal = new JournalAudit();

        if (utilisateurId != null) {
            utilisateurRepository.findById(utilisateurId)
                    .ifPresent(journal::setUtilisateur);
        }

        journal.setAction(action.trim());
        journal.setEntite(entite.trim());
        journal.setEntiteId(entiteId);
        journal.setAnciennesValeurs(anciennesValeurs);
        journal.setNouvellesValeurs(nouvellesValeurs);
        journal.setDateAction(LocalDateTime.now());

        journalAuditRepository.save(journal);
    }

    private JournalAuditResponse toResponse(JournalAudit journal) {
        Utilisateur utilisateur = journal.getUtilisateur();

        return new JournalAuditResponse(
                journal.getId(),
                utilisateur != null ? utilisateur.getId() : null,
                utilisateur != null ? utilisateur.getIdentifiant() : null,
                journal.getAction(),
                journal.getEntite(),
                journal.getEntiteId(),
                journal.getAnciennesValeurs(),
                journal.getNouvellesValeurs(),
                journal.getDateAction()
        );
    }
}