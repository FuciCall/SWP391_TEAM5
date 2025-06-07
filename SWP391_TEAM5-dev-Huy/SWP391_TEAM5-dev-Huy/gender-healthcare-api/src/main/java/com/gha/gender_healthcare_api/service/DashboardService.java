package com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.dto.response.DashboardOverviewDTO;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    public DashboardOverviewDTO getDashboardOverview() {
        // Dữ liệu mock, sau này có thể lấy từ DB qua repository
        long totalUsers = 500;
        long totalDonations = 1200;
        int activeCampaigns = 5;

        return new DashboardOverviewDTO(totalUsers, totalDonations, activeCampaigns);
    }
}
