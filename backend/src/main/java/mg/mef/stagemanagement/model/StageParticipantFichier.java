package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "stage_participant_fichiers",
    uniqueConstraints = @UniqueConstraint(columnNames = {"stage_participant_id", "fichier_id"})
)
public class StageParticipantFichier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stage_participant_id", nullable = false)
    private StageParticipant stageParticipant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fichier_id", nullable = false)
    private Fichier fichier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_piece_jointe_id", nullable = false)
    private TypePieceJointe typePieceJointe;

    @Column(name = "date_association", nullable = false)
    private LocalDateTime dateAssociation = LocalDateTime.now();

    public StageParticipantFichier() {
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

    public Fichier getFichier() {
        return fichier;
    }

    public void setFichier(Fichier fichier) {
        this.fichier = fichier;
    }

    public TypePieceJointe getTypePieceJointe() {
        return typePieceJointe;
    }

    public void setTypePieceJointe(TypePieceJointe typePieceJointe) {
        this.typePieceJointe = typePieceJointe;
    }

    public LocalDateTime getDateAssociation() {
        return dateAssociation;
    }

    public void setDateAssociation(LocalDateTime dateAssociation) {
        this.dateAssociation = dateAssociation;
    }

}
