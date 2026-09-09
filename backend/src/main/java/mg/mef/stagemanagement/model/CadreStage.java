package mg.mef.stagemanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cadres_stage")
public class CadreStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "theme_stage", nullable = false, length = 250)
    private String themeStage;

    @Column(name = "activite_confie_stagiaire", columnDefinition = "text")
    private String activiteConfieStagiaire;

    @Column(name = "objectif_stage", columnDefinition = "text")
    private String objectifStage;

    @Column(nullable = false)
    private Boolean actif = true;

    public CadreStage() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getThemeStage() {
        return themeStage;
    }

    public void setThemeStage(String themeStage) {
        this.themeStage = themeStage;
    }

    public String getActiviteConfieStagiaire() {
        return activiteConfieStagiaire;
    }

    public void setActiviteConfieStagiaire(String activiteConfieStagiaire) {
        this.activiteConfieStagiaire = activiteConfieStagiaire;
    }

    public String getObjectifStage() {
        return objectifStage;
    }

    public void setObjectifStage(String objectifStage) {
        this.objectifStage = objectifStage;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

}
