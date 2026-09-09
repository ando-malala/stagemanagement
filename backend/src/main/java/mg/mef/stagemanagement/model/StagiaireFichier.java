package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "stagiaire_fichiers",
    uniqueConstraints = @UniqueConstraint(columnNames = {"stagiaire_id", "fichier_id"})
)
public class StagiaireFichier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stagiaire_id", nullable = false)
    private Stagiaire stagiaire;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fichier_id", nullable = false)
    private Fichier fichier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_piece_jointe_id", nullable = false)
    private TypePieceJointe typePieceJointe;

    @Column(name = "date_association", nullable = false)
    private LocalDateTime dateAssociation = LocalDateTime.now();

    public StagiaireFichier() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Stagiaire getStagiaire() {
        return stagiaire;
    }

    public void setStagiaire(Stagiaire stagiaire) {
        this.stagiaire = stagiaire;
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
