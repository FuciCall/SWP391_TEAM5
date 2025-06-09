
package com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.dto.response.DashboardOverviewDTO;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    public DashboardOverviewDTO getDashboardOverview() {
        long totalUsers = 500L;
        long totalDonations = 1200L;
        int activeCampaigns = 5;
        return new DashboardOverviewDTO(totalUsers, totalDonations, activeCampaigns);
    }
}