package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import mg.mef.stagemanagement.model.enums.AlertePriorite;
import mg.mef.stagemanagement.model.enums.AlerteStatut;

@Entity
@Table(name = "alertes")
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_alerte_id", nullable = false)
    private TypeAlerte typeAlerte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_participant_id")
    private StageParticipant stageParticipant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rapport_id")
    private RapportActivite rapport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encadreur_id")
    private Encadreur encadreur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soutenance_id")
    private Soutenance soutenance;

    @Column(columnDefinition = "text")
    private String message;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertePriorite priorite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlerteStatut statut = AlerteStatut.A_TRAITER;

    @Column(name = "date_detection", nullable = false)
    private LocalDateTime dateDetection = LocalDateTime.now();

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traite_par_id")
    private Utilisateur traitePar;

    @Column(name = "cle_deduplication", unique = true, length = 255)
    private String cleDeduplication;

    public Alerte() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TypeAlerte getTypeAlerte() {
        return typeAlerte;
    }

    public void setTypeAlerte(TypeAlerte typeAlerte) {
        this.typeAlerte = typeAlerte;
    }

    public StageParticipant getStageParticipant() {
        return stageParticipant;
    }

    public void setStageParticipant(StageParticipant stageParticipant) {
        this.stageParticipant = stageParticipant;
    }

    public RapportActivite getRapport() {
        return rapport;
    }

    public void setRapport(RapportActivite rapport) {
        this.rapport = rapport;
    }

    public Encadreur getEncadreur() {
        return encadreur;
    }

    public void setEncadreur(Encadreur encadreur) {
        this.encadreur = encadreur;
    }

    public Soutenance getSoutenance() {
        return soutenance;
    }

    public void setSoutenance(Soutenance soutenance) {
        this.soutenance = soutenance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public AlertePriorite getPriorite() {
        return priorite;
    }

    public void setPriorite(AlertePriorite priorite) {
        this.priorite = priorite;
    }

    public AlerteStatut getStatut() {
        return statut;
    }

    public void setStatut(AlerteStatut statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateDetection() {
        return dateDetection;
    }

    public void setDateDetection(LocalDateTime dateDetection) {
        this.dateDetection = dateDetection;
    }

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }

    public Utilisateur getTraitePar() {
        return traitePar;
    }

    public void setTraitePar(Utilisateur traitePar) {
        this.traitePar = traitePar;
    }

    public String getCleDeduplication() {
        return cleDeduplication;
    }

    public void setCleDeduplication(String cleDeduplication) {
        this.cleDeduplication = cleDeduplication;
    }

}
