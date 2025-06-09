
package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.dto.response.DashboardOverviewDTO;
import com.gha.gender_healthcare_api.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/dashboard"})
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping({"/overview"})
    public ResponseEntity<DashboardOverviewDTO> getDashboardOverview() {
        DashboardOverviewDTO overview = this.dashboardService.getDashboardOverview();
        return ResponseEntity.ok(overview);
    }
}
