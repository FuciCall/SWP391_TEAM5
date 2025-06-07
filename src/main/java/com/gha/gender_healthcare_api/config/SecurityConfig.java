//: Cấu hình bảo mật Spring Security cho toàn bộ ứng dụng (JWT, OAuth2, filter, CORS...).
package com.gha.gender_healthcare_api.config;

import com.gha.gender_healthcare_api.security.CustomUserDetailsService;
import com.gha.gender_healthcare_api.security.JwtAuthenticationEntryPoint;
import com.gha.gender_healthcare_api.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

// THÊM CÁC IMPORTS SAU CHO OAUTH2 VÀ CUSTOM SUCCESS HANDLER
import com.gha.gender_healthcare_api.service.CustomOidcUserService;
import com.gha.gender_healthcare_api.service.CustomOAuth2UserService;
import com.gha.gender_healthcare_api.security.oauth2.CustomOAuth2AuthenticationSuccessHandler;
import com.gha.gender_healthcare_api.exception.CustomAuthenticationEntryPoint;

@Configuration // Đánh dấu lớp này là một lớp cấu hình Spring
@EnableWebSecurity // Kích hoạt bảo mật web của Spring Security
@EnableMethodSecurity(prePostEnabled = true) // Cho phép bảo mật cấp phương thức (ví dụ: @PreAuthorize)
public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService customUserDetailsService; // Dịch vụ tải thông tin người dùng

        @Autowired
        private CustomOAuth2UserService customOAuth2UserService; // Dịch vụ xử lý người dùng OAuth2 tùy chỉnh

        @Autowired
        private CustomOidcUserService customOidcUserService; // THÊM DÒNG NÀY

        @Autowired
        private JwtAuthenticationEntryPoint unauthorizedHandler; // Xử lý lỗi xác thực không thành công

        @Autowired
        private CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler; // AUTOWIRE CUSTOM
                                                                                                   // SUCCESS HANDLER
                                                                                                   // MỚI

        @Autowired
        private CustomAuthenticationEntryPoint customAuthenticationEntryPoint; // Xử lý lỗi xác thực rõ ràng

        @Bean // Định nghĩa một bean cho JwtAuthenticationFilter
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
                return new JwtAuthenticationFilter();
        }

        @Bean // Định nghĩa một bean cho PasswordEncoder (BCrypt)
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean // Định nghĩa một bean cho AuthenticationManager
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean // Định nghĩa một bean cho DaoAuthenticationProvider
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(customUserDetailsService); // Đặt dịch vụ người dùng
                authProvider.setPasswordEncoder(passwordEncoder()); // Đặt bộ mã hóa mật khẩu
                return authProvider;
        }

        @Bean // Định nghĩa chuỗi filter bảo mật
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(customAuthenticationEntryPoint)) // Sử dụng
                                                                                                           // custom
                                                                                                           // entry
                                                                                                           // point
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/auth/**", "/api-docs/**", "/swagger-ui/**",
                                                                "/swagger-ui.html", "/oauth2/**",
                                                                "/login/oauth2/code/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService)
                                                                .oidcUserService(customOidcUserService))
                                                .successHandler(customOAuth2AuthenticationSuccessHandler));

                http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
                http.authenticationProvider(authenticationProvider());
                http.cors(Customizer.withDefaults());

                return http.build();
        }

        @Bean // Định nghĩa một bean cho CorsFilter
        public CorsFilter corsFilter() {
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowCredentials(true);
                config.addAllowedOriginPattern("*"); // Cho phép mọi origin, có thể thay bằng origin cụ thể
                config.addAllowedHeader("*");
                config.addAllowedMethod("*");
                source.registerCorsConfiguration("/**", config);
                return new CorsFilter(source);
        }
}
