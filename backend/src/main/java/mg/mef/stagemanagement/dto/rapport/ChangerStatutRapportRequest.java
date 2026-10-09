package mg.mef.stagemanagement.dto.rapport;

public class ChangerStatutRapportRequest {

    private String statutCode;
    private String motif;

    public ChangerStatutRapportRequest() {
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