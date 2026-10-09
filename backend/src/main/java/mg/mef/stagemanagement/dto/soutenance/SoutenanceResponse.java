package mg.mef.stagemanagement.dto.soutenance;

import java.time.LocalDateTime;

public class SoutenanceResponse {

    private Integer id;
    private Integer stageParticipantId;
    private String stagiaire;
    private String encadreur;
    private LocalDateTime dateSoutenance;
    private String lieu;
    private String observation;
    private LocalDateTime dateCreation;

    public SoutenanceResponse() {
    }

    public SoutenanceResponse(
            Integer id,
            Integer stageParticipantId,
            String stagiaire,
            String encadreur,
            LocalDateTime dateSoutenance,
            String lieu,
            String observation,
            LocalDateTime dateCreation
    ) {
        this.id = id;
        this.stageParticipantId = stageParticipantId;
        this.stagiaire = stagiaire;
        this.encadreur = encadreur;
        this.dateSoutenance = dateSoutenance;
        this.lieu = lieu;
        this.observation = observation;
        this.dateCreation = dateCreation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStageParticipantId() {
        return stageParticipantId;
    }

    public void setStageParticipantId(Integer stageParticipantId) {
        this.stageParticipantId = stageParticipantId;
    }

    public String getStagiaire() {
        return stagiaire;
    }

    public void setStagiaire(String stagiaire) {
        this.stagiaire = stagiaire;
    }

    public String getEncadreur() {
        return encadreur;
    }

    public void setEncadreur(String encadreur) {
        this.encadreur = encadreur;
    }

    public LocalDateTime getDateSoutenance() {
        return dateSoutenance;
    }

    public void setDateSoutenance(LocalDateTime dateSoutenance) {
        this.dateSoutenance = dateSoutenance;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}