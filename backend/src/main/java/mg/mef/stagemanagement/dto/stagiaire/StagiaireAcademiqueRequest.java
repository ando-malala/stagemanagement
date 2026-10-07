package mg.mef.stagemanagement.dto.stagiaire;

public class StagiaireAcademiqueRequest {

    private Integer niveauEtudeId;
    private Integer filiereId;
    private Integer etablissementId;
    private String anneeEtudes;

    public StagiaireAcademiqueRequest() {
    }

    public Integer getNiveauEtudeId() {
        return niveauEtudeId;
    }

    public void setNiveauEtudeId(Integer niveauEtudeId) {
        this.niveauEtudeId = niveauEtudeId;
    }

    public Integer getFiliereId() {
        return filiereId;
    }

    public void setFiliereId(Integer filiereId) {
        this.filiereId = filiereId;
    }

    public Integer getEtablissementId() {
        return etablissementId;
    }

    public void setEtablissementId(Integer etablissementId) {
        this.etablissementId = etablissementId;
    }

    public String getAnneeEtudes() {
        return anneeEtudes;
    }

    public void setAnneeEtudes(String anneeEtudes) {
        this.anneeEtudes = anneeEtudes;
    }
}