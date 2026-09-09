package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stage_participant_evenements")
public class StageParticipantEvenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stage_participant_id", nullable = false)
    private StageParticipant stageParticipant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_evenement_id", nullable = false)
    private TypeEvenementParticipation typeEvenement;

    @Column(name = "date_evenement", nullable = false)
    private LocalDateTime dateEvenement = LocalDateTime.now();

    @Column(name = "ancienne_date_fin")
    private LocalDate ancienneDateFin;

    @Column(name = "nouvelle_date_fin")
    private LocalDate nouvelleDateFin;

    @Column(columnDefinition = "text")
    private String motif;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsable_id", nullable = false)
    private Utilisateur responsable;

    public StageParticipantEvenement() {
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

    public TypeEvenementParticipation getTypeEvenement() {
        return typeEvenement;
    }

    public void setTypeEvenement(TypeEvenementParticipation typeEvenement) {
        this.typeEvenement = typeEvenement;
    }

    public LocalDateTime getDateEvenement() {
        return dateEvenement;
    }

    public void setDateEvenement(LocalDateTime dateEvenement) {
        this.dateEvenement = dateEvenement;
    }

    public LocalDate getAncienneDateFin() {
        return ancienneDateFin;
    }

    public void setAncienneDateFin(LocalDate ancienneDateFin) {
        this.ancienneDateFin = ancienneDateFin;
    }

    public LocalDate getNouvelleDateFin() {
        return nouvelleDateFin;
    }

    public void setNouvelleDateFin(LocalDate nouvelleDateFin) {
        this.nouvelleDateFin = nouvelleDateFin;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Utilisateur getResponsable() {
        return responsable;
    }

    public void setResponsable(Utilisateur responsable) {
        this.responsable = responsable;
    }

}
