package mg.mef.stagemanagement.dto.audit;

import java.time.LocalDateTime;
import java.util.Map;

public class JournalAuditResponse {

    private Integer id;
    private Integer utilisateurId;
    private String utilisateur;
    private String action;
    private String entite;
    private Integer entiteId;
    private Map<String, Object> anciennesValeurs;
    private Map<String, Object> nouvellesValeurs;
    private LocalDateTime dateAction;

    public JournalAuditResponse() {
    }

    public JournalAuditResponse(
            Integer id,
            Integer utilisateurId,
            String utilisateur,
            String action,
            String entite,
            Integer entiteId,
            Map<String, Object> anciennesValeurs,
            Map<String, Object> nouvellesValeurs,
            LocalDateTime dateAction
    ) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.utilisateur = utilisateur;
        this.action = action;
        this.entite = entite;
        this.entiteId = entiteId;
        this.anciennesValeurs = anciennesValeurs;
        this.nouvellesValeurs = nouvellesValeurs;
        this.dateAction = dateAction;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntite() {
        return entite;
    }

    public void setEntite(String entite) {
        this.entite = entite;
    }

    public Integer getEntiteId() {
        return entiteId;
    }

    public void setEntiteId(Integer entiteId) {
        this.entiteId = entiteId;
    }

    public Map<String, Object> getAnciennesValeurs() {
        return anciennesValeurs;
    }

    public void setAnciennesValeurs(Map<String, Object> anciennesValeurs) {
        this.anciennesValeurs = anciennesValeurs;
    }

    public Map<String, Object> getNouvellesValeurs() {
        return nouvellesValeurs;
    }

    public void setNouvellesValeurs(Map<String, Object> nouvellesValeurs) {
        this.nouvellesValeurs = nouvellesValeurs;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }
}