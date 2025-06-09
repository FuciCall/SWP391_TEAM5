package com.gha.gender_healthcare_api.dto.response;

public class StatisticsDTO {
    private long totalUsers;
    private long totalDonations;
    private double averageAge;
    private double malePercentage;
    private double femalePercentage;

    public StatisticsDTO() {
    }

    public StatisticsDTO(long totalUsers, long totalDonations, double averageAge, double malePercentage, double femalePercentage) {
        this.totalUsers = totalUsers;
        this.totalDonations = totalDonations;
        this.averageAge = averageAge;
        this.malePercentage = malePercentage;
        this.femalePercentage = femalePercentage;
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

    public double getAverageAge() {
        return this.averageAge;
    }

    public void setAverageAge(double averageAge) {
        this.averageAge = averageAge;
    }

    public double getMalePercentage() {
        return this.malePercentage;
    }

    public void setMalePercentage(double malePercentage) {
        this.malePercentage = malePercentage;
    }

    public double getFemalePercentage() {
        return this.femalePercentage;
    }

    public void setFemalePercentage(double femalePercentage) {
        this.femalePercentage = femalePercentage;
    }
}