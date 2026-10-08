package mg.mef.stagemanagement.dto.stage;

public class StageRequest {

    private String libelle;
    private Integer structureAccueilId;

    public StageRequest() {
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
}