package mg.mef.stagemanagement.dto.rapport;

import java.time.LocalDate;

public class RapportActiviteRequest {

    private Integer stageParticipantId;
    private LocalDate debutSemaine;
    private LocalDate finSemaine;
    private String activite;
    private String difficultes;
    private String solutions;
    private Integer moisStageId;

    public RapportActiviteRequest() {
    }

    public Integer getStageParticipantId() {
        return stageParticipantId;
    }

    public void setStageParticipantId(Integer stageParticipantId) {
        this.stageParticipantId = stageParticipantId;
    }

    public LocalDate getDebutSemaine() {
        return debutSemaine;
    }

    public void setDebutSemaine(LocalDate debutSemaine) {
        this.debutSemaine = debutSemaine;
    }

    public LocalDate getFinSemaine() {
        return finSemaine;
    }

    public void setFinSemaine(LocalDate finSemaine) {
        this.finSemaine = finSemaine;
    }

    public String getActivite() {
        return activite;
    }

    public void setActivite(String activite) {
        this.activite = activite;
    }

    public String getDifficultes() {
        return difficultes;
    }

    public void setDifficultes(String difficultes) {
        this.difficultes = difficultes;
    }

    public String getSolutions() {
        return solutions;
    }

    public void setSolutions(String solutions) {
        this.solutions = solutions;
    }

    public Integer getMoisStageId() {
        return moisStageId;
    }

    public void setMoisStageId(Integer moisStageId) {
        this.moisStageId = moisStageId;
    }
}