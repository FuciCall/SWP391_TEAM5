package com.gha.gender_healthcare_api.dto.response;

import java.time.LocalDate;

public class DetailedReportDTO {
    private String userFullName;
    private String gender;
    private int age;
    private LocalDate donationDate;
    private String donationType;
    private String location;

    public DetailedReportDTO() {
    }

    public DetailedReportDTO(String userFullName, String gender, int age, LocalDate donationDate, String donationType, String location) {
        this.userFullName = userFullName;
        this.gender = gender;
        this.age = age;
        this.donationDate = donationDate;
        this.donationType = donationType;
        this.location = location;
    }

    public String getUserFullName() {
        return this.userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDate getDonationDate() {
        return this.donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    public String getDonationType() {
        return this.donationType;
    }

    public void setDonationType(String donationType) {
        this.donationType = donationType;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}