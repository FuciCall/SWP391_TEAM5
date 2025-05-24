package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.entity.Blog;
import com.gha.gender_healthcare_api.entity.ClinicInfo;
import com.gha.gender_healthcare_api.entity.Service;
import com.gha.gender_healthcare_api.repository.BlogRepository;
import com.gha.gender_healthcare_api.repository.ClinicInfoRepository;
import com.gha.gender_healthcare_api.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private ClinicInfoRepository clinicRepo;
    @Autowired
    private BlogRepository blogRepo;
    @Autowired
    private ServiceRepository serviceRepo;

    @GetMapping("/clinic-info")
    public ClinicInfo getClinicInfo() {
        return clinicRepo.findById(1L).orElse(null);
    }

    @GetMapping("/blogs")
    public List<Blog> getAllBlogs() {
        return blogRepo.findAll();
    }

    @GetMapping("/services")
    public List<Service> getAllServices() {
        return serviceRepo.findAll();
    }
}
