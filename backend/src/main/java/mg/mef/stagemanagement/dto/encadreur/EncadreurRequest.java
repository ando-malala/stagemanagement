package mg.mef.stagemanagement.dto.encadreur;

public class EncadreurRequest {

    private Integer utilisateurId;
    private String nom;
    private String prenoms;
    private String fonction;
    private String localite;
    private String porte;
    private String contact;
    private String courriel;
    private Integer departementId;
    private Integer capaciteMaxStagiaires;

    public EncadreurRequest() {
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenoms() {
        return prenoms;
    }

    public void setPrenoms(String prenoms) {
        this.prenoms = prenoms;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getLocalite() {
        return localite;
    }

    public void setLocalite(String localite) {
        this.localite = localite;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCourriel() {
        return courriel;
    }

    public void setCourriel(String courriel) {
        this.courriel = courriel;
    }

    public Integer getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Integer departementId) {
        this.departementId = departementId;
    }

    public Integer getCapaciteMaxStagiaires() {
        return capaciteMaxStagiaires;
    }

    public void setCapaciteMaxStagiaires(Integer capaciteMaxStagiaires) {
        this.capaciteMaxStagiaires = capaciteMaxStagiaires;
    }
}