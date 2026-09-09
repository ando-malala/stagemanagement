package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "rapports_activite",
    uniqueConstraints = @UniqueConstraint(columnNames = {"stage_participant_id", "debut_semaine", "fin_semaine"})
)
public class RapportActivite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stage_participant_id", nullable = false)
    private StageParticipant stageParticipant;

    @Column(name = "debut_semaine", nullable = false)
    private LocalDate debutSemaine;

    @Column(name = "fin_semaine", nullable = false)
    private LocalDate finSemaine;

    @Column(columnDefinition = "text")
    private String activite;

    @Column(columnDefinition = "text")
    private String difficultes;

    @Column(columnDefinition = "text")
    private String solutions;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mois_stage_id", nullable = false)
    private MoisStage moisStage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "statut_id", nullable = false)
    private Statut statut;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification = LocalDateTime.now();

    @Column(name = "date_soumission")
    private LocalDateTime dateSoumission;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    public RapportActivite() {
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

    public MoisStage getMoisStage() {
        return moisStage;
    }

    public void setMoisStage(MoisStage moisStage) {
        this.moisStage = moisStage;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public LocalDateTime getDateSoumission() {
        return dateSoumission;
    }

    public void setDateSoumission(LocalDateTime dateSoumission) {
        this.dateSoumission = dateSoumission;
    }

    public LocalDateTime getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(LocalDateTime dateValidation) {
        this.dateValidation = dateValidation;
    }

}
