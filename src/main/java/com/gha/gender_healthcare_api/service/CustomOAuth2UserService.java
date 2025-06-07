//: Xử lý thông tin người dùng khi đăng nhập bằng OAuth2 (Google), tạo mới hoặc lấy user từ DB.
package com.gha.gender_healthcare_api.service;

import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.repository.UserRepository;
import com.gha.gender_healthcare_api.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

        @Autowired
        private UserRepository userRepository;

        @Override
        @Transactional
        public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                OAuth2User oauth2User = super.loadUser(userRequest);
                String email = oauth2User.getAttribute("email");
                String googleId = oauth2User.getName();

                Optional<User> userOptional = userRepository.findByProviderAndProviderId("google", googleId);
                User user = userOptional.orElseGet(() -> {
                        User newUser = new User();
                        newUser.setProvider("google");
                        newUser.setProviderId(googleId);
                        newUser.setEmail(email);
                        newUser.setUsername(email);
                        newUser.setRole(User.Role.CUSTOMER);
                        newUser.setCreatedDate(LocalDateTime.now());
                        return userRepository.save(newUser);
                });

                return new CustomUserDetails(user, oauth2User.getAttributes());
        }
}