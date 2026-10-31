package mg.mef.stagemanagement.dto.stagiaire;

import java.time.LocalDateTime;

public class StagiaireResponse {

    private Integer departementId;
    public Integer getDepartementId() { return departementId; }
    public void setDepartementId(Integer value) { departementId = value; }
    private Integer id;
    private String nom;
    private String prenoms;
    private String adresse;
    private String contact;
    private String email;
    private String statut;
    private LocalDateTime dateCreation;

    public StagiaireResponse() {
    }

    public StagiaireResponse(
            Integer id,
            String nom,
            String prenoms,
            String adresse,
            String contact,
            String email,
            String statut,
            LocalDateTime dateCreation
    ) {
        this.id = id;
        this.nom = nom;
        this.prenoms = prenoms;
        this.adresse = adresse;
        this.contact = contact;
        this.email = email;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}