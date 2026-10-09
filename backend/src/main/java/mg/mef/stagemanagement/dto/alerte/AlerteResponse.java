package mg.mef.stagemanagement.dto.alerte;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AlerteResponse {

    private Integer id;

    private String typeAlerteCode;
    private String typeAlerteLibelle;
    private String actionRecommandee;

    private Integer stageParticipantId;
    private Integer rapportId;
    private Integer encadreurId;
    private Integer soutenanceId;

    private String message;
    private LocalDate dateEcheance;
    private String priorite;
    private String statut;

    private LocalDateTime dateDetection;
    private LocalDateTime dateTraitement;

    private Integer traiteParId;
    private String traitePar;

    public AlerteResponse() {
    }

    public AlerteResponse(
            Integer id,
            String typeAlerteCode,
            String typeAlerteLibelle,
            String actionRecommandee,
            Integer stageParticipantId,
            Integer rapportId,
            Integer encadreurId,
            Integer soutenanceId,
            String message,
            LocalDate dateEcheance,
            String priorite,
            String statut,
            LocalDateTime dateDetection,
            LocalDateTime dateTraitement,
            Integer traiteParId,
            String traitePar
    ) {
        this.id = id;
        this.typeAlerteCode = typeAlerteCode;
        this.typeAlerteLibelle = typeAlerteLibelle;
        this.actionRecommandee = actionRecommandee;
        this.stageParticipantId = stageParticipantId;
        this.rapportId = rapportId;
        this.encadreurId = encadreurId;
        this.soutenanceId = soutenanceId;
        this.message = message;
        this.dateEcheance = dateEcheance;
        this.priorite = priorite;
        this.statut = statut;
        this.dateDetection = dateDetection;
        this.dateTraitement = dateTraitement;
        this.traiteParId = traiteParId;
        this.traitePar = traitePar;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeAlerteCode() {
        return typeAlerteCode;
    }

    public void setTypeAlerteCode(String typeAlerteCode) {
        this.typeAlerteCode = typeAlerteCode;
    }

    public String getTypeAlerteLibelle() {
        return typeAlerteLibelle;
    }

    public void setTypeAlerteLibelle(String typeAlerteLibelle) {
        this.typeAlerteLibelle = typeAlerteLibelle;
    }

    public String getActionRecommandee() {
        return actionRecommandee;
    }

    public void setActionRecommandee(String actionRecommandee) {
        this.actionRecommandee = actionRecommandee;
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateDetection() {
        return dateDetection;
    }

    public void setDateDetection(LocalDateTime dateDetection) {
        this.dateDetection = dateDetection;
    }

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }

    public Integer getTraiteParId() {
        return traiteParId;
    }

    public void setTraiteParId(Integer traiteParId) {
        this.traiteParId = traiteParId;
    }

    public String getTraitePar() {
        return traitePar;
    }

    public void setTraitePar(String traitePar) {
        this.traitePar = traitePar;
    }
}