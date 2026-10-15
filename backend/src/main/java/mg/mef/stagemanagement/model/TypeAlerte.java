package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import mg.mef.stagemanagement.model.enums.NatureAlerte;

@Entity
@Table(name = "types_alerte")
public class TypeAlerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, unique = true, length = 150)
    private String libelle;

    @Column(name = "poids_priorite", nullable = false)
    private Integer poidsPriorite = 0;

    @Column(name = "action_recommandee", length = 250)
    private String actionRecommandee;

    @Column(nullable = false)
    private Boolean actif = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "nature_alerte", nullable = false, length = 20)
    private NatureAlerte natureAlerte;

    public TypeAlerte() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getPoidsPriorite() {
        return poidsPriorite;
    }

    public void setPoidsPriorite(Integer poidsPriorite) {
        this.poidsPriorite = poidsPriorite;
    }

    public String getActionRecommandee() {
        return actionRecommandee;
    }

    public void setActionRecommandee(String actionRecommandee) {
        this.actionRecommandee = actionRecommandee;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public NatureAlerte getNatureAlerte() {
        return natureAlerte;
    }

    public void setNatureAlerte(NatureAlerte natureAlerte) {
        this.natureAlerte = natureAlerte;
    }

}
