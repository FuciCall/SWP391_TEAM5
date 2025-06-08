package main.java.com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.dto.request.ServiceRequest;
import com.gha.gender_healthcare_api.dto.response.ServiceResponse;
import com.gha.gender_healthcare_api.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // Create new service
    @PostMapping
    public ResponseEntity<ServiceResponse> createService(@RequestBody ServiceRequest request) {
        return ResponseEntity.ok(serviceService.createService(request));
    }

    // Update full service (name, description, type, price)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateService(@PathVariable Long id,
                                                         @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(serviceService.updateService(id, request));
    }

    // Delete service by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    // Get list of all services
    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    // Update only price
    @PatchMapping("/{id}/price")
    public ResponseEntity<ServiceResponse> updatePrice(@PathVariable Long id,
                                                       @RequestParam Double newPrice) {
        return ResponseEntity.ok(serviceService.updatePrice(id, newPrice));
    }
}
