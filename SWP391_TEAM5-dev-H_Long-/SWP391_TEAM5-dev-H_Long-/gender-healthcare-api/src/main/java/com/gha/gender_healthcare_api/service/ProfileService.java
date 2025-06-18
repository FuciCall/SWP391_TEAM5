package com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.dto.response.UserProfileDTO;
import com.gha.gender_healthcare_api.dto.response.HistoryDTO;
import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

   public UserProfileDTO getUserProfile(Long userId) {
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isPresent()) {
        User user = userOpt.get();
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setGender(user.getGender());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }
    return null;
}

    public UserProfileDTO updateUserProfile(Long userId, UserProfileDTO profile) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return null;
        User user = userOpt.get();
        user.setFullName(profile.getFullName());
        user.setGender(profile.getGender());
        user.setDateOfBirth(profile.getDateOfBirth());
        user.setPhoneNumber(profile.getPhoneNumber());
        userRepository.save(user);
        return getUserProfile(userId);
    }

    public List<HistoryDTO> getUserHistory(Long userId) {
        // Dữ liệu mẫu, thực tế nên lấy từ DB
        List<HistoryDTO> list = new ArrayList<>();
        HistoryDTO h1 = new HistoryDTO();
        h1.setId(1L);
        h1.setType("Test");
        h1.setDescription("Xét nghiệm máu tổng quát");
        h1.setDate(java.time.LocalDate.of(2025, 5, 10));
        list.add(h1);

        HistoryDTO h2 = new HistoryDTO();
        h2.setId(2L);
        h2.setType("Consultation");
        h2.setDescription("Tư vấn sức khỏe định kỳ");
        h2.setDate(java.time.LocalDate.of(2025, 4, 20));
        list.add(h2);

        return list;
    }
}