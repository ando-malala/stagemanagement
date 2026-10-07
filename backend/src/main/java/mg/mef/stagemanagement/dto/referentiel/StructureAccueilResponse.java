package mg.mef.stagemanagement.dto.referentiel;

public class StructureAccueilResponse {

    private Integer id;
    private Integer departementId;
    private String departement;
    private String libelle;
    private String localite;
    private String porte;
    private String contact;
    private String courriel;
    private Boolean actif;

    public StructureAccueilResponse() {
    }

    public StructureAccueilResponse(
            Integer id,
            Integer departementId,
            String departement,
            String libelle,
            String localite,
            String porte,
            String contact,
            String courriel,
            Boolean actif
    ) {
        this.id = id;
        this.departementId = departementId;
        this.departement = departement;
        this.libelle = libelle;
        this.localite = localite;
        this.porte = porte;
        this.contact = contact;
        this.courriel = courriel;
        this.actif = actif;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
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

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}