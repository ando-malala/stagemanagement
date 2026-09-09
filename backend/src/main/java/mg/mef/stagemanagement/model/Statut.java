package mg.mef.stagemanagement.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "statuts",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type_statut_id", "code"}),
        @UniqueConstraint(columnNames = {"type_statut_id", "libelle"})
    }
)
public class Statut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String libelle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_statut_id", nullable = false)
    private TypeStatut typeStatut;

    @Column(nullable = false)
    private Boolean actif = true;

    public Statut() {
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

    public TypeStatut getTypeStatut() {
        return typeStatut;
    }

    public void setTypeStatut(TypeStatut typeStatut) {
        this.typeStatut = typeStatut;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

}
