package mg.mef.stagemanagement.dto.auth;

import java.util.List;

public class AuthMeResponse {

    private Integer utilisateurId;
    private String identifiant;
    private List<String> roles;

    public AuthMeResponse() {
    }

    public AuthMeResponse(
            Integer utilisateurId,
            String identifiant,
            List<String> roles
    ) {
        this.utilisateurId = utilisateurId;
        this.identifiant = identifiant;
        this.roles = roles;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}