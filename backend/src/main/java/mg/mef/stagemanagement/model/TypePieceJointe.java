package mg.mef.stagemanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "types_piece_jointe")
public class TypePieceJointe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, unique = true, length = 150)
    private String libelle;

    @Column(name = "pour_stagiaire", nullable = false)
    private Boolean pourStagiaire = false;

    @Column(name = "pour_participation", nullable = false)
    private Boolean pourParticipation = false;

    @Column(name = "obligatoire_cloture", nullable = false)
    private Boolean obligatoireCloture = false;

    @Column(name = "multiple_autorise", nullable = false)
    private Boolean multipleAutorise = true;

    @Column(nullable = false)
    private Boolean actif = true;

    public TypePieceJointe() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Boolean getPourStagiaire() {
        return pourStagiaire;
    }

    public void setPourStagiaire(Boolean pourStagiaire) {
        this.pourStagiaire = pourStagiaire;
    }

    public Boolean getPourParticipation() {
        return pourParticipation;
    }

    public void setPourParticipation(Boolean pourParticipation) {
        this.pourParticipation = pourParticipation;
    }

    public Boolean getObligatoireCloture() {
        return obligatoireCloture;
    }

    public void setObligatoireCloture(Boolean obligatoireCloture) {
        this.obligatoireCloture = obligatoireCloture;
    }

    public Boolean getMultipleAutorise() {
        return multipleAutorise;
    }

    public void setMultipleAutorise(Boolean multipleAutorise) {
        this.multipleAutorise = multipleAutorise;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

}
