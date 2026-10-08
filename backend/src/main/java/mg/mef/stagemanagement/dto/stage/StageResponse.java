package mg.mef.stagemanagement.dto.stage;

import java.time.LocalDateTime;

public class StageResponse {

    private Integer id;
    private String libelle;
    private Integer structureAccueilId;
    private String structureAccueil;
    private Integer departementId;
    private String departement;
    private LocalDateTime dateCreation;

    public StageResponse() {
    }

    public StageResponse(
            Integer id,
            String libelle,
            Integer structureAccueilId,
            String structureAccueil,
            Integer departementId,
            String departement,
            LocalDateTime dateCreation
    ) {
        this.id = id;
        this.libelle = libelle;
        this.structureAccueilId = structureAccueilId;
        this.structureAccueil = structureAccueil;
        this.departementId = departementId;
        this.departement = departement;
        this.dateCreation = dateCreation;
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

    public Integer getStructureAccueilId() {
        return structureAccueilId;
    }

    public void setStructureAccueilId(Integer structureAccueilId) {
        this.structureAccueilId = structureAccueilId;
    }

    public String getStructureAccueil() {
        return structureAccueil;
    }

    public void setStructureAccueil(String structureAccueil) {
        this.structureAccueil = structureAccueil;
    }

    public Integer getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Integer departementId) {
        this.departementId = departementId;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}