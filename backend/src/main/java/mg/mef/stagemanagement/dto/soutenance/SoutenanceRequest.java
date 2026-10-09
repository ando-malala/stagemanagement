package mg.mef.stagemanagement.dto.soutenance;

import java.time.LocalDateTime;

public class SoutenanceRequest {

    private Integer stageParticipantId;
    private LocalDateTime dateSoutenance;
    private String lieu;
    private String observation;

    public SoutenanceRequest() {
    }

    public Integer getStageParticipantId() {
        return stageParticipantId;
    }

    public void setStageParticipantId(Integer stageParticipantId) {
        this.stageParticipantId = stageParticipantId;
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
}