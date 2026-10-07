package mg.mef.stagemanagement.dto.referentiel;

public class CadreStageResponse {

    private Integer id;
    private String themeStage;
    private String activiteConfieStagiaire;
    private String objectifStage;
    private Boolean actif;

    public CadreStageResponse() {
    }

    public CadreStageResponse(
            Integer id,
            String themeStage,
            String activiteConfieStagiaire,
            String objectifStage,
            Boolean actif
    ) {
        this.id = id;
        this.themeStage = themeStage;
        this.activiteConfieStagiaire = activiteConfieStagiaire;
        this.objectifStage = objectifStage;
        this.actif = actif;
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