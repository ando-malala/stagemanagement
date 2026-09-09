package mg.mef.stagemanagement.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "stagiaire_academique",
    uniqueConstraints = @UniqueConstraint(columnNames = {"stagiaire_id", "niveau_etude_id", "filiere_id", "etablissement_id", "annee_etudes"})
)
public class StagiaireAcademique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stagiaire_id", nullable = false)
    private Stagiaire stagiaire;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "niveau_etude_id", nullable = false)
    private NiveauEtude niveauEtude;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etablissement_id", nullable = false)
    private Etablissement etablissement;

    @Column(name = "annee_etudes", nullable = false, length = 9)
    private String anneeEtudes;

    public StagiaireAcademique() {
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

    public NiveauEtude getNiveauEtude() {
        return niveauEtude;
    }

    public void setNiveauEtude(NiveauEtude niveauEtude) {
        this.niveauEtude = niveauEtude;
    }

    public Filiere getFiliere() {
        return filiere;
    }

    public void setFiliere(Filiere filiere) {
        this.filiere = filiere;
    }

    public Etablissement getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(Etablissement etablissement) {
        this.etablissement = etablissement;
    }

    public String getAnneeEtudes() {
        return anneeEtudes;
    }

    public void setAnneeEtudes(String anneeEtudes) {
        this.anneeEtudes = anneeEtudes;
    }

}
