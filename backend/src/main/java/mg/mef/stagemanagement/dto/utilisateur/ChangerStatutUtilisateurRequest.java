package mg.mef.stagemanagement.dto.utilisateur;

public class ChangerStatutUtilisateurRequest {

    private String statutCode;
    private String motif;

    public ChangerStatutUtilisateurRequest(){}

    public String getStatutCode() {
        return statutCode;
    }

    public void setStatutCode(String statutCode) {
        this.statutCode = statutCode;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }
}
