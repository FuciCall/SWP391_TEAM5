package com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.dto.response.DetailedReportDTO;
import com.gha.gender_healthcare_api.dto.response.StatisticsDTO;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    public StatisticsDTO getStatistics() {
        long totalUsers = 500L;
        long totalDonations = 1200L;
        double averageAge = 29.4;
        double malePercentage = 55.2;
        double femalePercentage = 44.8;
        return new StatisticsDTO(totalUsers, totalDonations, averageAge, malePercentage, femalePercentage);
    }

    public List<DetailedReportDTO> getDetailedReports() {
        return List.of(new DetailedReportDTO("Nguyễn Văn A", "Nam", 28, LocalDate.of(2025, 5, 12), "Hiến máu toàn phần", "Hà Nội"), new DetailedReportDTO("Trần Thị B", "Nữ", 32, LocalDate.of(2025, 4, 10), "Hiến tiểu cầu", "TP.HCM"), new DetailedReportDTO("Lê Văn C", "Nam", 24, LocalDate.of(2025, 3, 15), "Hiến huyết tương", "Đà Nẵng"));
    }
}
