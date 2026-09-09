package mg.mef.stagemanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "parametres_suivi")
public class ParametresSuivi {

    @Id
    private Short id = 1;

    @Column(name = "delai_validation_rapport_jours", nullable = false)
    private Integer delaiValidationRapportJours = 3;

    @Column(name = "capacite_encadreur_par_defaut", nullable = false)
    private Integer capaciteEncadreurParDefaut = 5;

    @Column(name = "taille_max_fichier_octets", nullable = false)
    private Long tailleMaxFichierOctets = 10485760L;

    public ParametresSuivi() {
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Integer getDelaiValidationRapportJours() {
        return delaiValidationRapportJours;
    }

    public void setDelaiValidationRapportJours(Integer delaiValidationRapportJours) {
        this.delaiValidationRapportJours = delaiValidationRapportJours;
    }

    public Integer getCapaciteEncadreurParDefaut() {
        return capaciteEncadreurParDefaut;
    }

    public void setCapaciteEncadreurParDefaut(Integer capaciteEncadreurParDefaut) {
        this.capaciteEncadreurParDefaut = capaciteEncadreurParDefaut;
    }

    public Long getTailleMaxFichierOctets() {
        return tailleMaxFichierOctets;
    }

    public void setTailleMaxFichierOctets(Long tailleMaxFichierOctets) {
        this.tailleMaxFichierOctets = tailleMaxFichierOctets;
    }

}
