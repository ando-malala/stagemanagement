package mg.mef.stagemanagement.dto.utilisateur;

public class AjouterDepartementUtilisateurRequest {

    private Integer departementId;

    public AjouterDepartementUtilisateurRequest() {
    }

    public Integer getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Integer departementId) {
        this.departementId = departementId;
    }
}