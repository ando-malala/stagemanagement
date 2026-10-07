package mg.mef.stagemanagement.dto.referentiel;

public class ReferentielResponse {

    private Integer id;
    private String libelle;
    private Boolean actif;

    public ReferentielResponse() {
    }

    public ReferentielResponse(Integer id, String libelle, Boolean actif) {
        this.id = id;
        this.libelle = libelle;
        this.actif = actif;
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