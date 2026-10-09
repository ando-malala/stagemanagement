package mg.mef.stagemanagement.dto.evaluation;

import java.time.LocalDateTime;

public class EvaluationFinaleResponse {

    private Integer id;
    private Integer stageParticipantId;
    private String stagiaire;
    private Integer encadreurId;
    private String encadreur;
    private String appreciationGlobale;
    private String observationsFinales;
    private Boolean estFinalisee;
    private LocalDateTime dateEvaluation;
    private LocalDateTime dateFinalisation;

    public EvaluationFinaleResponse() {
    }

    public EvaluationFinaleResponse(
            Integer id,
            Integer stageParticipantId,
            String stagiaire,
            Integer encadreurId,
            String encadreur,
            String appreciationGlobale,
            String observationsFinales,
            Boolean estFinalisee,
            LocalDateTime dateEvaluation,
            LocalDateTime dateFinalisation
    ) {
        this.id = id;
        this.stageParticipantId = stageParticipantId;
        this.stagiaire = stagiaire;
        this.encadreurId = encadreurId;
        this.encadreur = encadreur;
        this.appreciationGlobale = appreciationGlobale;
        this.observationsFinales = observationsFinales;
        this.estFinalisee = estFinalisee;
        this.dateEvaluation = dateEvaluation;
        this.dateFinalisation = dateFinalisation;
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

    public Integer getEncadreurId() {
        return encadreurId;
    }

    public void setEncadreurId(Integer encadreurId) {
        this.encadreurId = encadreurId;
    }

    public String getEncadreur() {
        return encadreur;
    }

    public void setEncadreur(String encadreur) {
        this.encadreur = encadreur;
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

    public Boolean getEstFinalisee() {
        return estFinalisee;
    }

    public void setEstFinalisee(Boolean estFinalisee) {
        this.estFinalisee = estFinalisee;
    }

    public LocalDateTime getDateEvaluation() {
        return dateEvaluation;
    }

    public void setDateEvaluation(LocalDateTime dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public LocalDateTime getDateFinalisation() {
        return dateFinalisation;
    }

    public void setDateFinalisation(LocalDateTime dateFinalisation) {
        this.dateFinalisation = dateFinalisation;
    }
}