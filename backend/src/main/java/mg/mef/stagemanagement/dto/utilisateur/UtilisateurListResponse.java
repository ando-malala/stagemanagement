package mg.mef.stagemanagement.dto.utilisateur;

public class UtilisateurListResponse {

    private Integer id;
    private String identifiant;
    private String statut;

    public UtilisateurListResponse() {
    }

    public UtilisateurListResponse(
            Integer id,
            String identifiant,
            String statut
    ) {
        this.id = id;
        this.identifiant = identifiant;
        this.statut = statut;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}