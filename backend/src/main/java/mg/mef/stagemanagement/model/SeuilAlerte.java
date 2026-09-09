package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import mg.mef.stagemanagement.model.enums.AlertePriorite;

@Entity
@Table(
    name = "seuils_alerte",
    uniqueConstraints = @UniqueConstraint(columnNames = {"type_alerte_id", "jours_avant"})
)
public class SeuilAlerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_alerte_id", nullable = false)
    private TypeAlerte typeAlerte;

    @Column(name = "jours_avant", nullable = false)
    private Integer joursAvant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertePriorite priorite;

    @Column(nullable = false)
    private Boolean actif = true;

    public SeuilAlerte() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TypeAlerte getTypeAlerte() {
        return typeAlerte;
    }

    public void setTypeAlerte(TypeAlerte typeAlerte) {
        this.typeAlerte = typeAlerte;
    }

    public Integer getJoursAvant() {
        return joursAvant;
    }

    public void setJoursAvant(Integer joursAvant) {
        this.joursAvant = joursAvant;
    }

    public AlertePriorite getPriorite() {
        return priorite;
    }

    public void setPriorite(AlertePriorite priorite) {
        this.priorite = priorite;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

}
