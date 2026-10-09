package mg.mef.stagemanagement.dto.rapport;

public class RapportObservationRequest {

    private String typeObservation;
    private String commentaire;
    private Boolean visibleStagiaire;

    public RapportObservationRequest() {
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
}