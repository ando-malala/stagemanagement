package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "journal_audit")
public class JournalAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(nullable = false, length = 100)
    private String entite;

    @Column(name = "entite_id")
    private Integer entiteId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "anciennes_valeurs", columnDefinition = "jsonb")
    private Map<String, Object> anciennesValeurs;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "nouvelles_valeurs", columnDefinition = "jsonb")
    private Map<String, Object> nouvellesValeurs;

    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction = LocalDateTime.now();

    public JournalAudit() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
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
