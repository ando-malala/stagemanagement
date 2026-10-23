
package mg.mef.stagemanagement.dto.stage;

import java.time.LocalDate;

public class EvenementParticipationRequest {

    private String motif;
    private LocalDate dateEffet;

    public EvenementParticipationRequest() {
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }
}
