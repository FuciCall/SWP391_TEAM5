package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.dto.response.DetailedReportDTO;
import com.gha.gender_healthcare_api.dto.response.StatisticsDTO;
import com.gha.gender_healthcare_api.service.ReportService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/reports"})
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping({"/statistics"})
    public ResponseEntity<StatisticsDTO> viewStatistics() {
        return ResponseEntity.ok(this.reportService.getStatistics());
    }

    @GetMapping({"/details"})
    public ResponseEntity<List<DetailedReportDTO>> viewDetailedReports() {
        return ResponseEntity.ok(this.reportService.getDetailedReports());
    }
}
