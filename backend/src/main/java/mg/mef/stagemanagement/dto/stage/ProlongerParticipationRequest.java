package mg.mef.stagemanagement.dto.stage;

import java.time.LocalDate;

public class ProlongerParticipationRequest {

    private LocalDate nouvelleDateFin;
    private String motif;

    public ProlongerParticipationRequest() {
    }

    public LocalDate getNouvelleDateFin() {
        return nouvelleDateFin;
    }

    public void setNouvelleDateFin(LocalDate nouvelleDateFin) {
        this.nouvelleDateFin = nouvelleDateFin;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }
}