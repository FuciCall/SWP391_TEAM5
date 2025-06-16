package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.entity.Specialty;
import com.gha.gender_healthcare_api.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @GetMapping
    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }
}
