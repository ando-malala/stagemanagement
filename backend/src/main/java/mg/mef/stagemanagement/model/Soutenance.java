package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "soutenances")
public class Soutenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stage_participant_id", nullable = false, unique = true)
    private StageParticipant stageParticipant;

    @Column(name = "date_soutenance", nullable = false)
    private LocalDateTime dateSoutenance;

    @Column(length = 250)
    private String lieu;

    @Column(columnDefinition = "text")
    private String observation;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    public Soutenance() {
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

    public LocalDateTime getDateSoutenance() {
        return dateSoutenance;
    }

    public void setDateSoutenance(LocalDateTime dateSoutenance) {
        this.dateSoutenance = dateSoutenance;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

}
