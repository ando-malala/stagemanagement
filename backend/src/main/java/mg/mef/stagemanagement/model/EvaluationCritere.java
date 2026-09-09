package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
    name = "evaluation_criteres",
    uniqueConstraints = @UniqueConstraint(columnNames = {"evaluation_id", "critere_id"})
)
public class EvaluationCritere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private EvaluationFinale evaluation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "critere_id", nullable = false)
    private CritereEvaluation critere;

    @Column(precision = 4, scale = 2)
    private BigDecimal note;

    @Column(columnDefinition = "text")
    private String commentaire;

    public EvaluationCritere() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EvaluationFinale getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationFinale evaluation) {
        this.evaluation = evaluation;
    }

    public CritereEvaluation getCritere() {
        return critere;
    }

    public void setCritere(CritereEvaluation critere) {
        this.critere = critere;
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
