package mg.mef.stagemanagement.dto.encadreur;

public class ChangerStatutEncadreurRequest {

    private String statutCode;
    private String motif;

    public ChangerStatutEncadreurRequest() {
    }

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