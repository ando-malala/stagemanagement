package mg.mef.stagemanagement.dto.stage;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StageParticipantResponse {

    private boolean peutRedigerRapport;

    public boolean isPeutRedigerRapport() { return peutRedigerRapport; }
    public void setPeutRedigerRapport(boolean value) { peutRedigerRapport = value; }

    private Integer id;

    private Integer stageId;
    private String stage;

    private Integer stagiaireAcademiqueId;
    private Integer stagiaireId;
    private String stagiaireNomComplet;
    private String niveauEtude;
    private String filiere;
    private String etablissement;
    private String anneeEtudes;

    private Integer cadreStageId;
    private String themeStage;

    private Integer encadreurId;
    private String encadreurNomComplet;

    private LocalDate dateDebutStage;
    private LocalDate dateFinStage;

    private String statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private LocalDateTime dateCloture;

    public StageParticipantResponse() {
    }

    public StageParticipantResponse(
            Integer id,
            Integer stageId,
            String stage,
            Integer stagiaireAcademiqueId,
            Integer stagiaireId,
            String stagiaireNomComplet,
            String niveauEtude,
            String filiere,
            String etablissement,
            String anneeEtudes,
            Integer cadreStageId,
            String themeStage,
            Integer encadreurId,
            String encadreurNomComplet,
            LocalDate dateDebutStage,
            LocalDate dateFinStage,
            String statut,
            LocalDateTime dateCreation,
            LocalDateTime dateModification,
            LocalDateTime dateCloture
    ) {
        this.id = id;
        this.stageId = stageId;
        this.stage = stage;
        this.stagiaireAcademiqueId = stagiaireAcademiqueId;
        this.stagiaireId = stagiaireId;
        this.stagiaireNomComplet = stagiaireNomComplet;
        this.niveauEtude = niveauEtude;
        this.filiere = filiere;
        this.etablissement = etablissement;
        this.anneeEtudes = anneeEtudes;
        this.cadreStageId = cadreStageId;
        this.themeStage = themeStage;
        this.encadreurId = encadreurId;
        this.encadreurNomComplet = encadreurNomComplet;
        this.dateDebutStage = dateDebutStage;
        this.dateFinStage = dateFinStage;
        this.statut = statut;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.dateCloture = dateCloture;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStageId() {
        return stageId;
    }

    public void setStageId(Integer stageId) {
        this.stageId = stageId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Integer getStagiaireAcademiqueId() {
        return stagiaireAcademiqueId;
    }

    public void setStagiaireAcademiqueId(Integer stagiaireAcademiqueId) {
        this.stagiaireAcademiqueId = stagiaireAcademiqueId;
    }

    public Integer getStagiaireId() {
        return stagiaireId;
    }

    public void setStagiaireId(Integer stagiaireId) {
        this.stagiaireId = stagiaireId;
    }

    public String getStagiaireNomComplet() {
        return stagiaireNomComplet;
    }

    public void setStagiaireNomComplet(String stagiaireNomComplet) {
        this.stagiaireNomComplet = stagiaireNomComplet;
    }

    public String getNiveauEtude() {
        return niveauEtude;
    }

    public void setNiveauEtude(String niveauEtude) {
        this.niveauEtude = niveauEtude;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(String etablissement) {
        this.etablissement = etablissement;
    }

    public String getAnneeEtudes() {
        return anneeEtudes;
    }

    public void setAnneeEtudes(String anneeEtudes) {
        this.anneeEtudes = anneeEtudes;
    }

    public Integer getCadreStageId() {
        return cadreStageId;
    }

    public void setCadreStageId(Integer cadreStageId) {
        this.cadreStageId = cadreStageId;
    }

    public String getThemeStage() {
        return themeStage;
    }

    public void setThemeStage(String themeStage) {
        this.themeStage = themeStage;
    }

    public Integer getEncadreurId() {
        return encadreurId;
    }

    public void setEncadreurId(Integer encadreurId) {
        this.encadreurId = encadreurId;
    }

    public String getEncadreurNomComplet() {
        return encadreurNomComplet;
    }

    public void setEncadreurNomComplet(String encadreurNomComplet) {
        this.encadreurNomComplet = encadreurNomComplet;
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

    public LocalDateTime getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(LocalDateTime dateCloture) {
        this.dateCloture = dateCloture;
    }
}
