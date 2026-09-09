package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations_finales")
public class EvaluationFinale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stage_participant_id", nullable = false, unique = true)
    private StageParticipant stageParticipant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encadreur_id", nullable = false)
    private Encadreur encadreur;

    @Column(name = "appreciation_globale", columnDefinition = "text")
    private String appreciationGlobale;

    @Column(name = "observations_finales", columnDefinition = "text")
    private String observationsFinales;

    @Column(name = "est_finalisee", nullable = false)
    private Boolean estFinalisee = false;

    @Column(name = "date_evaluation", nullable = false)
    private LocalDateTime dateEvaluation = LocalDateTime.now();

    @Column(name = "date_finalisation")
    private LocalDateTime dateFinalisation;

    public EvaluationFinale() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StageParticipant getStageParticipant() {
        return stageParticipant;
    }

    public void setStageParticipant(StageParticipant stageParticipant) {
        this.stageParticipant = stageParticipant;
    }

    public Encadreur getEncadreur() {
        return encadreur;
    }

    public void setEncadreur(Encadreur encadreur) {
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
