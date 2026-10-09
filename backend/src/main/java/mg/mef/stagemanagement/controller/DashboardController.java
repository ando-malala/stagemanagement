package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.dashboard.DashboardResumeResponse;
import mg.mef.stagemanagement.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resume")
    public ResponseEntity<DashboardResumeResponse> resume() {
        return ResponseEntity.ok(dashboardService.resume());
    }
}