package mg.mef.stagemanagement.dto.referentiel;

public class DepartementRequest {

    private String libelle;
    private String statutCode;

    public DepartementRequest() {
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getStatutCode() {
        return statutCode;
    }

    public void setStatutCode(String statutCode) {
        this.statutCode = statutCode;
    }
}