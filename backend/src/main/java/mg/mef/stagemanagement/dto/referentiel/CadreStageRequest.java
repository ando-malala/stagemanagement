package mg.mef.stagemanagement.dto.referentiel;

public class CadreStageRequest {

    private String themeStage;
    private String activiteConfieStagiaire;
    private String objectifStage;
    private Boolean actif;

    public CadreStageRequest() {
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