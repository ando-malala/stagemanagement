package mg.mef.stagemanagement.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "transitions_statut",
    uniqueConstraints = @UniqueConstraint(columnNames = {"statut_source_id", "statut_cible_id"})
)
public class TransitionStatut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "statut_source_id", nullable = false)
    private Statut statutSource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "statut_cible_id", nullable = false)
    private Statut statutCible;

    @Column(nullable = false)
    private Boolean actif = true;

    public TransitionStatut() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Statut getStatutSource() {
        return statutSource;
    }

    public void setStatutSource(Statut statutSource) {
        this.statutSource = statutSource;
    }

    public Statut getStatutCible() {
        return statutCible;
    }

    public void setStatutCible(Statut statutCible) {
        this.statutCible = statutCible;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

}
