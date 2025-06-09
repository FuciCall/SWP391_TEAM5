package com.gha.gender_healthcare_api.dto.response;

public class DashboardOverviewDTO {
    private long totalUsers;
    private long totalDonations;
    private int activeCampaigns;

    public DashboardOverviewDTO() {
    }

    public DashboardOverviewDTO(long totalUsers, long totalDonations, int activeCampaigns) {
        this.totalUsers = totalUsers;
        this.totalDonations = totalDonations;
        this.activeCampaigns = activeCampaigns;
    }

    public long getTotalUsers() {
        return this.totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalDonations() {
        return this.totalDonations;
    }

    public void setTotalDonations(long totalDonations) {
        this.totalDonations = totalDonations;
    }

    public int getActiveCampaigns() {
        return this.activeCampaigns;
    }

    public void setActiveCampaigns(int activeCampaigns) {
        this.activeCampaigns = activeCampaigns;
    }
}