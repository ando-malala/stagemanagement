package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stage_participant_statut_historique")
public class StageParticipantStatutHistorique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stage_participant_id", nullable = false)
    private StageParticipant stageParticipant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ancien_statut_id")
    private Statut ancienStatut;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "statut_id", nullable = false)
    private Statut statut;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsable_id", nullable = false)
    private Utilisateur responsable;

    @Column(columnDefinition = "text")
    private String motif;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification = LocalDateTime.now();

    public StageParticipantStatutHistorique() {
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

    public Statut getAncienStatut() {
        return ancienStatut;
    }

    public void setAncienStatut(Statut ancienStatut) {
        this.ancienStatut = ancienStatut;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Utilisateur getResponsable() {
        return responsable;
    }

    public void setResponsable(Utilisateur responsable) {
        this.responsable = responsable;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

}
