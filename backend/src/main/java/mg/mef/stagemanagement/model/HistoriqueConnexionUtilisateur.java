package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_connexion_utilisateur")
public class HistoriqueConnexionUtilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "date_connexion", nullable = false)
    private LocalDateTime dateConnexion = LocalDateTime.now();

    public HistoriqueConnexionUtilisateur() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDateTime getDateConnexion() {
        return dateConnexion;
    }

    public void setDateConnexion(LocalDateTime dateConnexion) {
        this.dateConnexion = dateConnexion;
    }

}
