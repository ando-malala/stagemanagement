package mg.mef.stagemanagement.dto.stage;

import java.time.LocalDate;

public class StageParticipantRequest {

    private Integer stageId;
    private Integer stagiaireAcademiqueId;
    private Integer cadreStageId;
    private Integer encadreurId;
    private LocalDate dateDebutStage;
    private LocalDate dateFinStage;

    public StageParticipantRequest() {
    }

    public Integer getStageId() {
        return stageId;
    }

    public void setStageId(Integer stageId) {
        this.stageId = stageId;
    }

    public Integer getStagiaireAcademiqueId() {
        return stagiaireAcademiqueId;
    }

    public void setStagiaireAcademiqueId(Integer stagiaireAcademiqueId) {
        this.stagiaireAcademiqueId = stagiaireAcademiqueId;
    }

    public Integer getCadreStageId() {
        return cadreStageId;
    }

    public void setCadreStageId(Integer cadreStageId) {
        this.cadreStageId = cadreStageId;
    }

    public Integer getEncadreurId() {
        return encadreurId;
    }

    public void setEncadreurId(Integer encadreurId) {
        this.encadreurId = encadreurId;
    }

    public LocalDate getDateDebutStage() {
        return dateDebutStage;
    }

    public void setDateDebutStage(LocalDate dateDebutStage) {
        this.dateDebutStage = dateDebutStage;
    }

    public LocalDate getDateFinStage() {
        return dateFinStage;
    }

    public void setDateFinStage(LocalDate dateFinStage) {
        this.dateFinStage = dateFinStage;
    }
}