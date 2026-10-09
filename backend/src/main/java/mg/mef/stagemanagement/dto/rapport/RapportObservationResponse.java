package mg.mef.stagemanagement.dto.rapport;

import java.time.LocalDateTime;

public class RapportObservationResponse {

    private Integer id;
    private Integer rapportId;
    private Integer auteurId;
    private String auteur;
    private String typeObservation;
    private String commentaire;
    private Boolean visibleStagiaire;
    private LocalDateTime dateCreation;

    public RapportObservationResponse() {
    }

    public RapportObservationResponse(
            Integer id,
            Integer rapportId,
            Integer auteurId,
            String auteur,
            String typeObservation,
            String commentaire,
            Boolean visibleStagiaire,
            LocalDateTime dateCreation
    ) {
        this.id = id;
        this.rapportId = rapportId;
        this.auteurId = auteurId;
        this.auteur = auteur;
        this.typeObservation = typeObservation;
        this.commentaire = commentaire;
        this.visibleStagiaire = visibleStagiaire;
        this.dateCreation = dateCreation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRapportId() {
        return rapportId;
    }

    public void setRapportId(Integer rapportId) {
        this.rapportId = rapportId;
    }

    public Integer getAuteurId() {
        return auteurId;
    }

    public void setAuteurId(Integer auteurId) {
        this.auteurId = auteurId;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getTypeObservation() {
        return typeObservation;
    }

    public void setTypeObservation(String typeObservation) {
        this.typeObservation = typeObservation;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Boolean getVisibleStagiaire() {
        return visibleStagiaire;
    }

    public void setVisibleStagiaire(Boolean visibleStagiaire) {
        this.visibleStagiaire = visibleStagiaire;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}