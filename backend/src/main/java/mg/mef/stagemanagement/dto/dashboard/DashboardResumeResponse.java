package mg.mef.stagemanagement.dto.dashboard;

public class DashboardResumeResponse {

    private long totalStagiaires;
    private long totalStages;
    private long totalParticipations;
    private long totalRapports;
    private long totalAlertesATraiter;
    private long totalAlertesTraitees;

    public DashboardResumeResponse() {
    }

    public DashboardResumeResponse(
            long totalStagiaires,
            long totalStages,
            long totalParticipations,
            long totalRapports,
            long totalAlertesATraiter,
            long totalAlertesTraitees
    ) {
        this.totalStagiaires = totalStagiaires;
        this.totalStages = totalStages;
        this.totalParticipations = totalParticipations;
        this.totalRapports = totalRapports;
        this.totalAlertesATraiter = totalAlertesATraiter;
        this.totalAlertesTraitees = totalAlertesTraitees;
    }

    public long getTotalStagiaires() {
        return totalStagiaires;
    }

    public void setTotalStagiaires(long totalStagiaires) {
        this.totalStagiaires = totalStagiaires;
    }

    public long getTotalStages() {
        return totalStages;
    }

    public void setTotalStages(long totalStages) {
        this.totalStages = totalStages;
    }

    public long getTotalParticipations() {
        return totalParticipations;
    }

    public void setTotalParticipations(long totalParticipations) {
        this.totalParticipations = totalParticipations;
    }

    public long getTotalRapports() {
        return totalRapports;
    }

    public void setTotalRapports(long totalRapports) {
        this.totalRapports = totalRapports;
    }

    public long getTotalAlertesATraiter() {
        return totalAlertesATraiter;
    }

    public void setTotalAlertesATraiter(long totalAlertesATraiter) {
        this.totalAlertesATraiter = totalAlertesATraiter;
    }

    public long getTotalAlertesTraitees() {
        return totalAlertesTraitees;
    }

    public void setTotalAlertesTraitees(long totalAlertesTraitees) {
        this.totalAlertesTraitees = totalAlertesTraitees;
    }
}