package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.model.*;
import mg.mef.stagemanagement.repository.*;
import java.util.List;
import java.util.Comparator;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ReferentielWorkflowService {
    public record Reference(Integer id, String code, String libelle) {}
    public record Transition(Integer id, String source, String cible, String libelle) {}
    private final StatutRepository statuts;
    private final TypeStatutRepository types;
    private final TransitionStatutRepository transitions;
    private final TypeEvenementParticipationRepository evenements;
    private final TypeAlerteRepository alertes;
    public ReferentielWorkflowService(StatutRepository statuts, TypeStatutRepository types,
            TransitionStatutRepository transitions, TypeEvenementParticipationRepository evenements, TypeAlerteRepository alertes) {
        this.statuts = statuts; this.types = types; this.transitions = transitions; this.evenements = evenements; this.alertes = alertes;
    }
    private String type(String code) {
        String normalized = code.toUpperCase(Locale.ROOT);
        if (types.findAll().stream().noneMatch(t -> normalized.equals(t.getCode()) && Boolean.TRUE.equals(t.getActif())))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de statut introuvable ou inactif");
        return normalized;
    }
    public List<Reference> statuts(String type) {
        String code = type(type);
        return statuts.findAll().stream().filter(s -> Boolean.TRUE.equals(s.getActif()) && code.equals(s.getTypeStatut().getCode()))
                .sorted(Comparator.comparing(Statut::getLibelle)).map(s -> new Reference(s.getId(), s.getCode(), s.getLibelle())).toList();
    }
    public List<Transition> transitions(String type, String source) {
        String code = type(type);
        Statut statut = statuts.findStatutByTypeCodeAndStatutCode(code, source.toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Statut source introuvable ou inactif"));
        return transitions.findAll().stream().filter(t -> Boolean.TRUE.equals(t.getActif())
                && t.getStatutSource().getId().equals(statut.getId()) && Boolean.TRUE.equals(t.getStatutCible().getActif())
                && code.equals(t.getStatutCible().getTypeStatut().getCode()))
                .sorted(Comparator.comparing(t -> t.getStatutCible().getCode()))
                .map(t -> new Transition(t.getId(), source.toUpperCase(Locale.ROOT), t.getStatutCible().getCode(), t.getStatutCible().getLibelle())).toList();
    }
    public List<Reference> evenements() {
        return evenements.findAll().stream().filter(t -> Boolean.TRUE.equals(t.getActif()))
                .sorted(Comparator.comparing(TypeEvenementParticipation::getLibelle))
                .map(t -> new Reference(t.getId(), t.getCode(), t.getLibelle())).toList();
    }
    public List<Reference> alertes() {
        return alertes.findAll().stream().filter(t -> Boolean.TRUE.equals(t.getActif()))
                .sorted(Comparator.comparing(TypeAlerte::getLibelle))
                .map(t -> new Reference(t.getId(), t.getCode(), t.getLibelle())).toList();
    }
}
