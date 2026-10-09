package mg.mef.stagemanagement.dto.alerte;

import java.time.LocalDate;

public class AlerteRequest {

    private String typeAlerteCode;
    private Integer stageParticipantId;
    private Integer rapportId;
    private Integer encadreurId;
    private Integer soutenanceId;
    private String message;
    private LocalDate dateEcheance;
    private String priorite;
    private String cleDeduplication;

    public AlerteRequest() {
    }

    public String getTypeAlerteCode() {
        return typeAlerteCode;
    }

    public void setTypeAlerteCode(String typeAlerteCode) {
        this.typeAlerteCode = typeAlerteCode;
    }

    public Integer getStageParticipantId() {
        return stageParticipantId;
    }

    public void setStageParticipantId(Integer stageParticipantId) {
        this.stageParticipantId = stageParticipantId;
    }

    public Integer getRapportId() {
        return rapportId;
    }

    public void setRapportId(Integer rapportId) {
        this.rapportId = rapportId;
    }

    public Integer getEncadreurId() {
        return encadreurId;
    }

    public void setEncadreurId(Integer encadreurId) {
        this.encadreurId = encadreurId;
    }

    public Integer getSoutenanceId() {
        return soutenanceId;
    }

    public void setSoutenanceId(Integer soutenanceId) {
        this.soutenanceId = soutenanceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public String getCleDeduplication() {
        return cleDeduplication;
    }

    public void setCleDeduplication(String cleDeduplication) {
        this.cleDeduplication = cleDeduplication;
    }
}