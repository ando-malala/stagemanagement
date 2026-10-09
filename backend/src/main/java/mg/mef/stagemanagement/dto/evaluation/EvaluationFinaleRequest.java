package mg.mef.stagemanagement.dto.evaluation;

public class EvaluationFinaleRequest {

    private Integer stageParticipantId;
    private Integer encadreurId;
    private String appreciationGlobale;
    private String observationsFinales;

    public EvaluationFinaleRequest() {
    }

    public Integer getStageParticipantId() {
        return stageParticipantId;
    }

    public void setStageParticipantId(Integer stageParticipantId) {
        this.stageParticipantId = stageParticipantId;
    }

    public Integer getEncadreurId() {
        return encadreurId;
    }

    public void setEncadreurId(Integer encadreurId) {
        this.encadreurId = encadreurId;
    }

    public String getAppreciationGlobale() {
        return appreciationGlobale;
    }

    public void setAppreciationGlobale(String appreciationGlobale) {
        this.appreciationGlobale = appreciationGlobale;
    }

    public String getObservationsFinales() {
        return observationsFinales;
    }

    public void setObservationsFinales(String observationsFinales) {
        this.observationsFinales = observationsFinales;
    }
}