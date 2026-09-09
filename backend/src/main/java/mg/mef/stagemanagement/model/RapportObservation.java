package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import mg.mef.stagemanagement.model.enums.TypeObservationRapport;

@Entity
@Table(name = "rapport_observations")
public class RapportObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rapport_id", nullable = false)
    private RapportActivite rapport;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_observation", nullable = false, length = 30)
    private TypeObservationRapport typeObservation;

    @Column(nullable = false, columnDefinition = "text")
    private String commentaire;

    @Column(name = "visible_stagiaire", nullable = false)
    private Boolean visibleStagiaire = true;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    public RapportObservation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RapportActivite getRapport() {
        return rapport;
    }

    public void setRapport(RapportActivite rapport) {
        this.rapport = rapport;
    }

    public Utilisateur getAuteur() {
        return auteur;
    }

    public void setAuteur(Utilisateur auteur) {
        this.auteur = auteur;
    }

    public TypeObservationRapport getTypeObservation() {
        return typeObservation;
    }

    public void setTypeObservation(TypeObservationRapport typeObservation) {
        this.typeObservation = typeObservation;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Boolean getVisibleStagiaire() {
        return visibleStagiaire;
    }

    public void setVisibleStagiaire(Boolean visibleStagiaire) {
        this.visibleStagiaire = visibleStagiaire;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

}
