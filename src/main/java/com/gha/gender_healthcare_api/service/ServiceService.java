/*
 * Service xử lý nghiệp vụ cho entity Service: thêm, sửa, xóa.
 */

package com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.entity.Service;
import com.gha.gender_healthcare_api.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public Service addService(Service service) {
        return serviceRepository.save(service);
    }

    public Service updateService(Long id, Service updated) {
        Service s = serviceRepository.findById(id)
                .orElseThrow(
                        () -> new NoSuchElementException("Cập nhật thất bại do không tìm thấy dịch vụ với id = " + id));
        s.setName(updated.getName());
        s.setDescription(updated.getDescription());
        s.setType(updated.getType());
        s.setPrice(updated.getPrice());
        return serviceRepository.save(s);
    }

    public void updatePrice(Long id, Double price) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cập nhật giá thất bại do không tìm thấy dịch vụ với id = " + id));
        service.setPrice(price);
        serviceRepository.save(service);
    }

    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new NoSuchElementException("Không tìm thấy dịch vụ với id = " + id);
        }
        serviceRepository.deleteById(id);
    }
}