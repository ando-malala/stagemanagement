package mg.mef.stagemanagement.dto.utilisateur;

public class DepartementUtilisateurResponse {

    private Integer id;
    private String libelle;
    private String statut;

    public DepartementUtilisateurResponse() {
    }

    public DepartementUtilisateurResponse(Integer id, String libelle, String statut) {
        this.id = id;
        this.libelle = libelle;
        this.statut = statut;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}