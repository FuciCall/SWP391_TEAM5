package main.java.com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.dto.request.ServiceRequest;
import com.gha.gender_healthcare_api.dto.response.ServiceResponse;
import com.gha.gender_healthcare_api.entity.Service;
import com.gha.gender_healthcare_api.repository.ServiceRepository;
import com.gha.gender_healthcare_api.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public ServiceResponse createService(ServiceRequest request) {
        Service service = new Service();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setType(request.getType());
        service.setPrice(request.getPrice());
        return mapToResponse(serviceRepository.save(service));
    }

    @Override
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        Service service = serviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service not found"));
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setType(request.getType());
        service.setPrice(request.getPrice());
        return mapToResponse(serviceRepository.save(service));
    }

    @Override
    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll()
            .stream().map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public ServiceResponse updatePrice(Long id, Double newPrice) {
        Service service = serviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service not found"));
        service.setPrice(newPrice);
        return mapToResponse(serviceRepository.save(service));
    }

    private ServiceResponse mapToResponse(Service s) {
        ServiceResponse res = new ServiceResponse();
        res.setId(s.getId());
        res.setName(s.getName());
        res.setDescription(s.getDescription());
        res.setType(s.getType());
        res.setPrice(s.getPrice());
        return res;
    }
}
