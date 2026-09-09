package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "stage_participant",
    uniqueConstraints = @UniqueConstraint(columnNames = {"stage_id", "stagiaire_academique_id", "date_debut_stage"})
)
public class StageParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stagiaire_academique_id", nullable = false)
    private StagiaireAcademique stagiaireAcademique;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cadre_stage_id", nullable = false)
    private CadreStage cadreStage;

    @Column(name = "date_debut_stage", nullable = false)
    private LocalDate dateDebutStage;

    @Column(name = "date_fin_stage", nullable = false)
    private LocalDate dateFinStage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encadreur_id", nullable = false)
    private Encadreur encadreur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "statut_id", nullable = false)
    private Statut statut;

    @Column(name = "date_cloture")
    private LocalDateTime dateCloture;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification = LocalDateTime.now();

    public StageParticipant() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public StagiaireAcademique getStagiaireAcademique() {
        return stagiaireAcademique;
    }

    public void setStagiaireAcademique(StagiaireAcademique stagiaireAcademique) {
        this.stagiaireAcademique = stagiaireAcademique;
    }

    public CadreStage getCadreStage() {
        return cadreStage;
    }

    public void setCadreStage(CadreStage cadreStage) {
        this.cadreStage = cadreStage;
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

    public Encadreur getEncadreur() {
        return encadreur;
    }

    public void setEncadreur(Encadreur encadreur) {
        this.encadreur = encadreur;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(LocalDateTime dateCloture) {
        this.dateCloture = dateCloture;
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

}
