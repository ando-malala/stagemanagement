package mg.mef.stagemanagement.dto.rapport;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RapportActiviteResponse {

    private boolean peutRediger;
    private boolean peutExaminer;

    public boolean isPeutRediger() { return peutRediger; }
    public void setPeutRediger(boolean value) { peutRediger = value; }
    public boolean isPeutExaminer() { return peutExaminer; }
    public void setPeutExaminer(boolean value) { peutExaminer = value; }

    private Integer id;

    private Integer stageParticipantId;
    private String stagiaire;
    private String encadreur;

    private LocalDate debutSemaine;
    private LocalDate finSemaine;

    private String activite;
    private String difficultes;
    private String solutions;

    private Integer moisStageId;
    private String moisStage;

    private String statut;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private LocalDateTime dateSoumission;
    private LocalDateTime dateValidation;

    public RapportActiviteResponse() {
    }

    public RapportActiviteResponse(
            Integer id,
            Integer stageParticipantId,
            String stagiaire,
            String encadreur,
            LocalDate debutSemaine,
            LocalDate finSemaine,
            String activite,
            String difficultes,
            String solutions,
            Integer moisStageId,
            String moisStage,
            String statut,
            LocalDateTime dateCreation,
            LocalDateTime dateModification,
            LocalDateTime dateSoumission,
            LocalDateTime dateValidation
    ) {
        this.id = id;
        this.stageParticipantId = stageParticipantId;
        this.stagiaire = stagiaire;
        this.encadreur = encadreur;
        this.debutSemaine = debutSemaine;
        this.finSemaine = finSemaine;
        this.activite = activite;
        this.difficultes = difficultes;
        this.solutions = solutions;
        this.moisStageId = moisStageId;
        this.moisStage = moisStage;
        this.statut = statut;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.dateSoumission = dateSoumission;
        this.dateValidation = dateValidation;
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

    public String getEncadreur() {
        return encadreur;
    }

    public void setEncadreur(String encadreur) {
        this.encadreur = encadreur;
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

    public String getMoisStage() {
        return moisStage;
    }

    public void setMoisStage(String moisStage) {
        this.moisStage = moisStage;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
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
