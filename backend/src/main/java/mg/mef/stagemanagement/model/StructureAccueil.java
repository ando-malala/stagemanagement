package mg.mef.stagemanagement.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "structures_accueil",
    uniqueConstraints = @UniqueConstraint(columnNames = {"departement_id", "libelle"})
)
public class StructureAccueil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departement_id", nullable = false)
    private Departement departement;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(length = 150)
    private String localite;

    @Column(length = 30)
    private String porte;

    @Column(length = 150)
    private String contact;

    @Column(length = 150)
    private String courriel;

    @Column(nullable = false)
    private Boolean actif = true;

    public StructureAccueil() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getLocalite() {
        return localite;
    }

    public void setLocalite(String localite) {
        this.localite = localite;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCourriel() {
        return courriel;
    }

    public void setCourriel(String courriel) {
        this.courriel = courriel;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

}
