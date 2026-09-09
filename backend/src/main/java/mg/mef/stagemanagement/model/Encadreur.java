package mg.mef.stagemanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "encadreurs")
public class Encadreur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", unique = true)
    private Utilisateur utilisateur;

    @Column(nullable = false, length = 250)
    private String nom;

    @Column(nullable = false, length = 250)
    private String prenoms;

    @Column(length = 150)
    private String fonction;

    @Column(length = 150)
    private String localite;

    @Column(length = 30)
    private String porte;

    @Column(length = 150)
    private String contact;

    @Column(length = 150)
    private String courriel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "statut_id", nullable = false)
    private Statut statut;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departement_id", nullable = false)
    private Departement departement;

    @Column(name = "capacite_max_stagiaires")
    private Integer capaciteMaxStagiaires;

    public Encadreur() {
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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenoms() {
        return prenoms;
    }

    public void setPrenoms(String prenoms) {
        this.prenoms = prenoms;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
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

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public Integer getCapaciteMaxStagiaires() {
        return capaciteMaxStagiaires;
    }

    public void setCapaciteMaxStagiaires(Integer capaciteMaxStagiaires) {
        this.capaciteMaxStagiaires = capaciteMaxStagiaires;
    }

}
