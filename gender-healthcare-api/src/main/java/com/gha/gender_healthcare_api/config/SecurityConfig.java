package com.gha.gender_healthcare_api.config;

import com.gha.gender_healthcare_api.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService customUserDetailsService;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable()) // Tắt CSRF nếu dùng REST API hoặc chưa cần
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/consultant/**").hasRole("CONSULTANT")
                                                .requestMatchers("/manager/**").hasRole("MANAGER")
                                                .requestMatchers("/staff/**").hasRole("STAFF")
                                                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/home", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll())
                                .userDetailsService(customUserDetailsService); // cấu hình service để Spring Security sử
                                                                               // dụng

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(); // dùng để mã hóa mật khẩu
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
