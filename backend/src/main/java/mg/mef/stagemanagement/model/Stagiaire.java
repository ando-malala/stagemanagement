package mg.mef.stagemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stagiaires")
public class Stagiaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(nullable = false, length = 250)
    private String prenoms;

    @Column(nullable = false, length = 250)
    private String adresse;

    @Column(length = 100)
    private String contact;

    @Column(length = 150)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "statut_id", nullable = false)
    private Statut statut;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", unique = true)
    private Utilisateur utilisateur;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_fichier_id", unique = true)
    private Fichier photoFichier;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id")
    private Departement departement;

    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement value) { departement = value; }

    public Stagiaire() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Fichier getPhotoFichier() {
        return photoFichier;
    }

    public void setPhotoFichier(Fichier photoFichier) {
        this.photoFichier = photoFichier;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

}
