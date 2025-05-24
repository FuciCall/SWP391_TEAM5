package com.gha.gender_healthcare_api.config;

import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.entity.User.Role;
import com.gha.gender_healthcare_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("bob", passwordEncoder.encode("bob"), Role.CUSTOMER));
            userRepository.save(new User("consultant01", passwordEncoder.encode("password2"), Role.CONSULTANT));
            userRepository.save(new User("staff01", passwordEncoder.encode("password3"), Role.STAFF));
            userRepository.save(new User("manager01", passwordEncoder.encode("password4"), Role.MANAGER));
            userRepository.save(new User("admin01", passwordEncoder.encode("password5"), Role.ADMIN));
        }
    }
}
