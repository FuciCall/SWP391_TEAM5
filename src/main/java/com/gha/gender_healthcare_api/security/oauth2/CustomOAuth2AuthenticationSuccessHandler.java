//: Xử lý sau khi đăng nhập OAuth2 thành công, tạo JWT và chuyển hướng về FE với token.
package com.gha.gender_healthcare_api.security.oauth2;

import com.gha.gender_healthcare_api.security.JwtTokenProvider;
import com.gha.gender_healthcare_api.security.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2AuthenticationSuccessHandler.class);

        @Autowired
        private JwtTokenProvider tokenProvider;

        @Value("${app.frontend.url}")
        private String frontendUrl;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
                Object principal = authentication.getPrincipal();
                // LOG CỰC KỲ QUAN TRỌNG: In ra loại Principal mà SuccessHandler nhận được
                logger.info("DEBUG: Loại Principal mà CustomOAuth2AuthenticationSuccessHandler nhận được: {}",
                                principal.getClass().getName());

                String targetUrl = frontendUrl;
                String username = null;
                String role = null;
                Long userId = null;
                String email = null;

                if (principal instanceof CustomUserDetails) {
                        CustomUserDetails customUserDetails = (CustomUserDetails) principal;
                        username = customUserDetails.getUsername();
                        role = customUserDetails.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.joining(","));
                        userId = customUserDetails.getId();
                        email = customUserDetails.getEmail();
                        logger.info("DEBUG: Principal là CustomUserDetails. Username: {}, UserId: {}, Email: {}",
                                        username, userId, email);

                        String jwt = tokenProvider.generateToken(authentication);
                        logger.info("DEBUG: JWT đã được tạo cho người dùng: {}", username);

                        targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                                        .queryParam("token", jwt)
                                        .build().toUriString();
                        logger.info("DEBUG: Đang chuyển hướng đến: {}", targetUrl);

                } else if (principal instanceof OAuth2User) {
                        // Đây là trường hợp mà chúng ta đang cố gắng tránh, nhưng vẫn xử lý để log rõ
                        // ràng
                        OAuth2User oauth2User = (OAuth2User) principal;
                        email = oauth2User.getAttribute("email");
                        logger.error("LỖI: Principal là OAuth2User (mặc định), không phải CustomUserDetails. Email: {}",
                                        email);
                        logger.error("Vấn đề có thể nằm ở CustomOAuth2UserService không trả về CustomUserDetails, hoặc cấu hình SecurityConfig.");

                        targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/error")
                                        .queryParam("error", "oauth2_principal_type_mismatch")
                                        .queryParam("principalType", principal.getClass().getName())
                                        .build().toUriString();
                } else {
                        logger.error("LỖI: Loại Principal không được hỗ trợ trong OAuth2 success handler: {}",
                                        principal.getClass().getName());
                        targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/error")
                                        .queryParam("error", "unsupported_principal_type")
                                        .build().toUriString();
                }

                getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
}
