package mg.mef.stagemanagement.dto.evaluation;

import java.math.BigDecimal;

public class EvaluationCritereRequest {

    private Integer critereId;
    private BigDecimal note;
    private String commentaire;

    public EvaluationCritereRequest() {
    }

    public Integer getCritereId() {
        return critereId;
    }

    public void setCritereId(Integer critereId) {
        this.critereId = critereId;
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