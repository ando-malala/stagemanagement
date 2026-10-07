package mg.mef.stagemanagement.dto.referentiel;

public class ReferentielRequest {

    private String libelle;
    private Boolean actif;

    public ReferentielRequest() {
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