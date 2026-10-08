package mg.mef.stagemanagement.dto.stage;

public class ChangerStatutParticipationRequest {

    private String statutCode;
    private String motif;

    public ChangerStatutParticipationRequest() {
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