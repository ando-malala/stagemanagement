package mg.mef.stagemanagement.dto.encadreur;

public class EncadreurResponse {

    private Integer id;
    private Integer utilisateurId;
    private String identifiantUtilisateur;
    private String nom;
    private String prenoms;
    private String fonction;
    private String localite;
    private String porte;
    private String contact;
    private String courriel;
    private String statut;
    private Integer departementId;
    private String departement;
    private Integer capaciteMaxStagiaires;

    public EncadreurResponse() {
    }

    public EncadreurResponse(
            Integer id,
            Integer utilisateurId,
            String identifiantUtilisateur,
            String nom,
            String prenoms,
            String fonction,
            String localite,
            String porte,
            String contact,
            String courriel,
            String statut,
            Integer departementId,
            String departement,
            Integer capaciteMaxStagiaires
    ) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.identifiantUtilisateur = identifiantUtilisateur;
        this.nom = nom;
        this.prenoms = prenoms;
        this.fonction = fonction;
        this.localite = localite;
        this.porte = porte;
        this.contact = contact;
        this.courriel = courriel;
        this.statut = statut;
        this.departementId = departementId;
        this.departement = departement;
        this.capaciteMaxStagiaires = capaciteMaxStagiaires;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getIdentifiantUtilisateur() {
        return identifiantUtilisateur;
    }

    public void setIdentifiantUtilisateur(String identifiantUtilisateur) {
        this.identifiantUtilisateur = identifiantUtilisateur;
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Integer getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Integer departementId) {
        this.departementId = departementId;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public Integer getCapaciteMaxStagiaires() {
        return capaciteMaxStagiaires;
    }

    public void setCapaciteMaxStagiaires(Integer capaciteMaxStagiaires) {
        this.capaciteMaxStagiaires = capaciteMaxStagiaires;
    }
}