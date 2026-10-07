package mg.mef.stagemanagement.dto.stagiaire;

public class StagiaireAcademiqueResponse {

    private Integer id;
    private Integer stagiaireId;
    private Integer niveauEtudeId;
    private String niveauEtude;
    private Integer filiereId;
    private String filiere;
    private Integer etablissementId;
    private String etablissement;
    private String anneeEtudes;

    public StagiaireAcademiqueResponse() {
    }

    public StagiaireAcademiqueResponse(
            Integer id,
            Integer stagiaireId,
            Integer niveauEtudeId,
            String niveauEtude,
            Integer filiereId,
            String filiere,
            Integer etablissementId,
            String etablissement,
            String anneeEtudes
    ) {
        this.id = id;
        this.stagiaireId = stagiaireId;
        this.niveauEtudeId = niveauEtudeId;
        this.niveauEtude = niveauEtude;
        this.filiereId = filiereId;
        this.filiere = filiere;
        this.etablissementId = etablissementId;
        this.etablissement = etablissement;
        this.anneeEtudes = anneeEtudes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStagiaireId() {
        return stagiaireId;
    }

    public void setStagiaireId(Integer stagiaireId) {
        this.stagiaireId = stagiaireId;
    }

    public Integer getNiveauEtudeId() {
        return niveauEtudeId;
    }

    public void setNiveauEtudeId(Integer niveauEtudeId) {
        this.niveauEtudeId = niveauEtudeId;
    }

    public String getNiveauEtude() {
        return niveauEtude;
    }

    public void setNiveauEtude(String niveauEtude) {
        this.niveauEtude = niveauEtude;
    }

    public Integer getFiliereId() {
        return filiereId;
    }

    public void setFiliereId(Integer filiereId) {
        this.filiereId = filiereId;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public Integer getEtablissementId() {
        return etablissementId;
    }

    public void setEtablissementId(Integer etablissementId) {
        this.etablissementId = etablissementId;
    }

    public String getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(String etablissement) {
        this.etablissement = etablissement;
    }

    public String getAnneeEtudes() {
        return anneeEtudes;
    }

    public void setAnneeEtudes(String anneeEtudes) {
        this.anneeEtudes = anneeEtudes;
    }
}