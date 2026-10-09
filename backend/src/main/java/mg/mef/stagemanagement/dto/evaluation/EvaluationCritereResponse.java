package mg.mef.stagemanagement.dto.evaluation;

import java.math.BigDecimal;

public class EvaluationCritereResponse {

    private Integer id;
    private Integer evaluationId;
    private Integer critereId;
    private String critereCode;
    private String critereLibelle;
    private BigDecimal coefficient;
    private BigDecimal note;
    private String commentaire;

    public EvaluationCritereResponse() {
    }

    public EvaluationCritereResponse(
            Integer id,
            Integer evaluationId,
            Integer critereId,
            String critereCode,
            String critereLibelle,
            BigDecimal coefficient,
            BigDecimal note,
            String commentaire
    ) {
        this.id = id;
        this.evaluationId = evaluationId;
        this.critereId = critereId;
        this.critereCode = critereCode;
        this.critereLibelle = critereLibelle;
        this.coefficient = coefficient;
        this.note = note;
        this.commentaire = commentaire;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(Integer evaluationId) {
        this.evaluationId = evaluationId;
    }

    public Integer getCritereId() {
        return critereId;
    }

    public void setCritereId(Integer critereId) {
        this.critereId = critereId;
    }

    public String getCritereCode() {
        return critereCode;
    }

    public void setCritereCode(String critereCode) {
        this.critereCode = critereCode;
    }

    public String getCritereLibelle() {
        return critereLibelle;
    }

    public void setCritereLibelle(String critereLibelle) {
        this.critereLibelle = critereLibelle;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    public BigDecimal getNote() {
        return note;
    }

    public void setNote(BigDecimal note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}