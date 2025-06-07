//: Xử lý thông tin người dùng khi đăng nhập bằng OIDC (Google), tạo mới hoặc lấy user từ DB.
package com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.repository.UserRepository;
import com.gha.gender_healthcare_api.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getEmail();
        String googleId = oidcUser.getSubject();

        // 1. Kiểm tra user theo email
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            // Đã tồn tại user với email này
            user = userOptional.get();
            // Nếu user chưa có providerId hoặc providerId khác thì cập nhật
            if (!"google".equals(user.getProvider()) || !googleId.equals(user.getProviderId())) {
                user.setProvider("google");
                user.setProviderId(googleId);
                userRepository.save(user);
            }
        } else {
            // 2. Chưa có user, tạo mới
            String fullName = oidcUser.getFullName();
            if (fullName == null) {
                Object nameObj = oidcUser.getClaims().get("name");
                fullName = nameObj != null ? nameObj.toString() : email;
            }
            user = new User();
            user.setProvider("google");
            user.setProviderId(googleId);
            user.setEmail(email);
            user.setUsername(email);
            user.setFullName(fullName);
            user.setRole(User.Role.CUSTOMER);
            user.setCreatedDate(LocalDateTime.now());
            user.setPassword("");
            userRepository.save(user);
        }

        return new CustomUserDetails(user, oidcUser.getClaims());
    }
}