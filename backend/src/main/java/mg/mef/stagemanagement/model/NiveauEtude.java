package mg.mef.stagemanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "niveaux_etude")
public class NiveauEtude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String libelle;

    @Column(nullable = false)
    private Boolean actif = true;

    public NiveauEtude() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

}
