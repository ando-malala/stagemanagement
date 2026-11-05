
package mg.mef.stagemanagement.dto.stage;

import java.time.LocalDate;

public class EvenementParticipationRequest {

    private String motif;
    private LocalDate dateEffet;
    private Boolean ignorerDocumentsManquants;
    private Boolean forcerCloture;

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

    public Boolean getIgnorerDocumentsManquants() {
        return ignorerDocumentsManquants;
    }

    public void setIgnorerDocumentsManquants(Boolean ignorerDocumentsManquants) {
        this.ignorerDocumentsManquants = ignorerDocumentsManquants;
    }

    public Boolean getForcerCloture() {
        return forcerCloture;
    }

    public void setForcerCloture(Boolean forcerCloture) {
        this.forcerCloture = forcerCloture;
    }
}
