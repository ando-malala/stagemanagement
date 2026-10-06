package mg.mef.stagemanagement.dto.utilisateur;

public class AjouterRoleUtilisateurRequest {

    private String roleCode;

    public AjouterRoleUtilisateurRequest() {
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}